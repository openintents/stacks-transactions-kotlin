package org.blockstack.android.stackstransactions

import org.blockstack.android.stackstransactions.message.StacksTransaction
import org.openintents.blockstack.stackstransactions.PrivateKey

class TransactionSigner(val transaction: StacksTransaction) {
    var sigHash:String = transaction.signBegin()
    var originDone:Boolean = false
    var checkOversign:Boolean = true
    var checkOverlap:Boolean = true

    fun signOrigin(privKey: PrivateKey) {
        if (this.checkOverlap && this.originDone) {
            throw Error("Cannot sign origin after sponsor key");
        }
        if (this.transaction.auth == null) {
            throw Error("\"transaction.auth\" is undefined");
        }
        if (this.transaction.auth.spendingCondition == null) {
            throw Error("\"transaction.auth.spendingCondition\" is undefined");
        }
        if (this.transaction.auth.spendingCondition.signaturesRequired == null) {
            throw Error("\"transaction.auth.spendingCondition.signaturesRequired\" is undefined");
        }
        if (
                this.checkOversign &&
                this.transaction.auth.spendingCondition.numSignatures() >=
                this.transaction.auth.spendingCondition.signaturesRequired
        ) {
            throw Error("Origin would have too many signatures");
        }

        val nextSighash = this.transaction.signNextOrigin(this.sigHash, privKey);
        this.sigHash = nextSighash;

    }

}
