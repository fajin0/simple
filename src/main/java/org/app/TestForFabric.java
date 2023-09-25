/*
 * SPDX-License-Identifier: Apache-2.0
 */

package main.java.org.app;

import java.math.BigInteger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "test",
        info = @Info(
                title = "TestForFabric",
                description = "A Simple Storage Test Contract For Fabric",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        ))
@Default
public final class TestForFabric implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Initial storageData = 5
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        StorageData storedData = new StorageData(5);
        String sortedJson = genson.serialize(storedData);
        stub.putStringState("storageData", sortedJson);
    }

    /**
     * set a new value of storedData
     *
     * @param ctx the transaction context
     * @param x the storedData value
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public int setValueOfFabric(final Context ctx, final int x) {
        ChaincodeStub stub = ctx.getStub();

        StorageData storageData = new StorageData(x);
        String sortedJson = genson.serialize(storageData);
        stub.putStringState("storageData", sortedJson);

        return x;
    }

    /**
     * query the value of the storedData before setValueOfFabric()
     *
     * @param ctx the transaction context
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String setValueOfFabric_query(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String storedData = stub.getStringState("storageData");

        return genson.deserialize(storedData, String.class);
    }

    /**
     * revoke the value of the storedData after setValueOfFabric()
     *
     * @param ctx the transaction context
     * @param x the storedData value
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public int setValueOfFabric_revoke(final Context ctx, final int x) {
        ChaincodeStub stub = ctx.getStub();

        StorageData storageData = new StorageData(x);
        String sortedJson = genson.serialize(storageData);
        stub.putStringState("storageData", sortedJson);

        return x;
    }

    /**
     * query the value of the storedData
     *
     * @param ctx the transaction context
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getValueOfFabric(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String storedData = stub.getStringState("storageData");

        return genson.deserialize(storedData, String.class);
    }

}
