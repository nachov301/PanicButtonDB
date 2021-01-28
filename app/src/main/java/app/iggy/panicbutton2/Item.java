package app.iggy.panicbutton2;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String TAG = "Item";
    private String title;
    private String description;
    private int image;

    public Item(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
