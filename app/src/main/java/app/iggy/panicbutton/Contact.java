package app.iggy.panicbutton;

import java.io.Serializable;

class Contact implements Serializable {
    public static final long serialVersion = 20161120L;
    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    public Contact(long id, String name, String description, int sortOrder) {
        this.m_Id = id;
        mName = name;
        mDescription = description;
        mSortOrder = sortOrder;
    }

    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder='" + mSortOrder + '\'' +
                '}';
    }
}
