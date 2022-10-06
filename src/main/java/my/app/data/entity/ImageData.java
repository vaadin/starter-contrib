package my.app.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class ImageData extends AbstractEntity {

    @Lob
    @Column(length = 1000000)
    private byte[] data;

    public ImageData() {
    }

    public ImageData(byte[] data) {
        this.data = data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

}
