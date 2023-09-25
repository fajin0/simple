package main.java.org.app;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;


@DataType()
public class StorageData {
    @Property()
    private final int storageData;

    public StorageData(@JsonProperty("storageData") final int storageData) {
        this.storageData = storageData;
    }

    public int getStorageData() {
        return storageData;
    }
}
