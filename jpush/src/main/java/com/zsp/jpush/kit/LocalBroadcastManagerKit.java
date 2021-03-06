package com.zsp.jpush.kit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

/**
 * @decs: LocalBroadcastManagerKit
 * @author: 郑少鹏
 * @date: 2019/5/31 16:54
 */
public final class LocalBroadcastManagerKit {
    private static final String TAG = "LocalBroadcastManager";
    private static final Object M_LOCK = new Object();
    private static LocalBroadcastManagerKit mInstance;
    private final Context mAppContext;
    private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap<>();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<>();
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();
    private final Handler mHandler;

    private LocalBroadcastManagerKit(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    LocalBroadcastManagerKit.this.executePendingBroadcasts();
                } else {
                    super.handleMessage(msg);
                }
            }
        };
    }

    public static LocalBroadcastManagerKit getInstance(Context context) {
        synchronized (M_LOCK) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManagerKit(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    @SuppressWarnings("rawtypes")
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        synchronized (this.mReceivers) {
            ReceiverRecord entry = new ReceiverRecord(filter, receiver);
            ArrayList filters = this.mReceivers.get(receiver);
            if (filters == null) {
                filters = new ArrayList(1);
                this.mReceivers.put(receiver, filters);
            }
            filters.add(filter);
            for (int i = 0; i < filter.countActions(); ++i) {
                String action = filter.getAction(i);
                ArrayList entries = this.mActions.get(action);
                if (entries == null) {
                    entries = new ArrayList(1);
                    this.mActions.put(action, entries);
                }
                entries.add(entry);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void unregisterReceiver(BroadcastReceiver receiver) {
        synchronized (this.mReceivers) {
            ArrayList filters = this.mReceivers.remove(receiver);
            if (filters != null) {
                for (int i = 0; i < filters.size(); ++i) {
                    IntentFilter filter = (IntentFilter) filters.get(i);
                    for (int j = 0; j < filter.countActions(); ++j) {
                        String action = filter.getAction(j);
                        ArrayList receivers = this.mActions.get(action);
                        if (receivers != null) {
                            for (int k = 0; k < receivers.size(); ++k) {
                                if (((ReceiverRecord) receivers.get(k)).receiver == receiver) {
                                    receivers.remove(k);
                                    --k;
                                }
                            }
                            if (receivers.size() <= 0) {
                                this.mActions.remove(action);
                            }
                        }
                    }
                }

            }
        }
    }

    @SuppressWarnings("rawtypes")
    public boolean sendBroadcast(Intent intent) {
        synchronized (this.mReceivers) {
            String action = intent.getAction();
            String type = intent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
            Uri data = intent.getData();
            String scheme = intent.getScheme();
            Set categories = intent.getCategories();
            boolean debug = (intent.getFlags() & 8) != 0;
            if (debug) {
                Timber.d("Resolving type %s scheme %s of intent %s", type, scheme, intent);
            }
            ArrayList entries = this.mActions.get(intent.getAction());
            if (entries != null) {
                if (debug) {
                    Timber.d("Action list: %s", entries);
                }
                ArrayList receivers = null;
                int i;
                for (i = 0; i < entries.size(); ++i) {
                    ReceiverRecord receiver = (ReceiverRecord) entries.get(i);
                    if (debug) {
                        Timber.d("Matching against filter %s", receiver.filter);
                    }
                    if (receiver.broadcasting) {
                        if (debug) {
                            Timber.d("  Filter's target already added");
                        }
                    } else {
                        int match = receiver.filter.match(action, type, scheme, data, categories, TAG);
                        if (match >= 0) {
                            if (debug) {
                                Timber.d("Filter matched! match=0x %s", Integer.toHexString(match));
                            }
                            if (receivers == null) {
                                receivers = new ArrayList();
                            }
                            receivers.add(receiver);
                            receiver.broadcasting = true;
                        } else if (debug) {
                            String reason;
                            switch (match) {
                                case -4:
                                    reason = "category";
                                    break;
                                case -3:
                                    reason = "action";
                                    break;
                                case -2:
                                    reason = "data";
                                    break;
                                case -1:
                                    reason = "type";
                                    break;
                                default:
                                    reason = "unknown reason";
                            }
                            Timber.d("Filter did not match: %s", reason);
                        }
                    }
                }
                if (receivers != null) {
                    for (i = 0; i < receivers.size(); ++i) {
                        ((ReceiverRecord) receivers.get(i)).broadcasting = false;
                    }
                    this.mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));
                    if (!this.mHandler.hasMessages(1)) {
                        this.mHandler.sendEmptyMessage(1);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public void sendBroadcastSync(Intent intent) {
        if (this.sendBroadcast(intent)) {
            this.executePendingBroadcasts();
        }
    }

    private void executePendingBroadcasts() {
        while (true) {
            BroadcastRecord[] brs;
            synchronized (this.mReceivers) {
                int br = this.mPendingBroadcasts.size();
                if (br <= 0) {
                    return;
                }
                brs = new BroadcastRecord[br];
                this.mPendingBroadcasts.toArray(brs);
                this.mPendingBroadcasts.clear();
            }
            for (BroadcastRecord var7 : brs) {
                for (int j = 0; j < var7.receivers.size(); ++j) {
                    var7.receivers.get(j).receiver.onReceive(this.mAppContext, var7.intent);
                }
            }
        }
    }

    private static class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent intent, ArrayList<ReceiverRecord> receiverRecords) {
            this.intent = intent;
            this.receivers = receiverRecords;
        }
    }

    private static class ReceiverRecord {
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        boolean broadcasting;

        ReceiverRecord(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
            this.filter = intentFilter;
            this.receiver = broadcastReceiver;
        }

        @NonNull
        @Override
        public String toString() {
            return "Receiver{" +
                    this.receiver +
                    " filter=" +
                    this.filter +
                    "}";
        }
    }
}