package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @decs: 本地广播管理器
 * @author: 郑少鹏
 * @date: 2019/5/31 16:54
 */
public final class LocalBroadcastManager {
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static final boolean DEBUG = false;
    private static final Object M_LOCK = new Object();
    private static LocalBroadcastManager mInstance;
    private final Context mAppContext;
    private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap<>();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<>();
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();
    private final Handler mHandler;

    private LocalBroadcastManager(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    LocalBroadcastManager.this.executePendingBroadcasts();
                } else {
                    super.handleMessage(msg);
                }
            }
        };
    }

    public static LocalBroadcastManager getInstance(Context context) {
        Object var1 = M_LOCK;
        synchronized (M_LOCK) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        HashMap var3 = this.mReceivers;
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

    public void unregisterReceiver(BroadcastReceiver receiver) {
        HashMap var2 = this.mReceivers;
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

    public boolean sendBroadcast(Intent intent) {
        HashMap var2 = this.mReceivers;
        synchronized (this.mReceivers) {
            String action = intent.getAction();
            String type = intent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
            Uri data = intent.getData();
            String scheme = intent.getScheme();
            Set categories = intent.getCategories();
            boolean debug = (intent.getFlags() & 8) != 0;
            if (debug) {
                Logger.v(TAG, "Resolving type " + type + " scheme " + scheme + " of intent " + intent);
            }
            ArrayList entries = this.mActions.get(intent.getAction());
            if (entries != null) {
                if (debug) {
                    Logger.v(TAG, "Action list: " + entries);
                }
                ArrayList receivers = null;
                int i;
                for (i = 0; i < entries.size(); ++i) {
                    ReceiverRecord receiver = (ReceiverRecord) entries.get(i);
                    if (debug) {
                        Logger.v(TAG, "Matching against filter " + receiver.filter);
                    }
                    if (receiver.broadcasting) {
                        if (debug) {
                            Logger.v(TAG, "  Filter\'s target already added");
                        }
                    } else {
                        int match = receiver.filter.match(action, type, scheme, data, categories, TAG);
                        if (match >= 0) {
                            if (debug) {
                                Logger.v(TAG, "  Filter matched!  match=0x" + Integer.toHexString(match));
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

                            Logger.v(TAG, "  Filter did not match: " + reason);
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
            HashMap i = this.mReceivers;
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

        @Override
        public String toString() {
            String builder = "Receiver{" +
                    this.receiver +
                    " filter=" +
                    this.filter +
                    "}";
            return builder;
        }
    }
}