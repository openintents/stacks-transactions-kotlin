package org.openintents.blockstack.stackstransactions

import org.junit.Test
import org.komputing.khash.sha256.Sha256
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import java.security.MessageDigest

internal class StacksTestImpl : StacksTest() {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun makeOnAndroid() {
        println(MessageDigest.getInstance("SHA-256")
            .digest("03ef788b3830c00abe8f64f62dc32fc863bc0b2cafeb073b6c8e1c7657d9c2c3ab".hexToByteArray()).toNoPrefixHexString())
        println(Sha256.digest("03ef788b3830c00abe8f64f62dc32fc863bc0b2cafeb073b6c8e1c7657d9c2c3ab".hexToByteArray()).toNoPrefixHexString())
        println("make on Android")
    }
}
