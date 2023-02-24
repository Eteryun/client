package com.eteryun.event;

import java.util.ArrayList;

public class Event {
    protected boolean isCanceled = false;

    public boolean isCancelable() {
        return false;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean cancel) {
        if (!isCancelable())
            throw new UnsupportedOperationException(
                    "Attempted to call Event#setCanceled() on a non-cancelable event of type: "
                            + this.getClass().getCanonicalName());

        isCanceled = cancel;
    }

    public Event call() {
        final ArrayList<EventData> datalist = EventManager.get(this.getClass());

        if (datalist != null) {
            for (EventData data : datalist) {
                try {
                    data.target.invoke(data.source, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
}
