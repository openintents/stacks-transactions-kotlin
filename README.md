# Multiplatform Stacks Transactions library

![Blockstack](blockstack.png)

This library provides methods to interact with the [Stacks Blockchain](https://testnet.blockstack.org)

## Installation
This is a kotlin multiplatform library, just add the required version as a dependency, e.g. for Android from jitpack
```
implementation("com.github.openintents:stacks-transactions-kotlin:<commit>")
```
## Usage
### Create Account
Any key derived using the sec256k1 elliptic curve can be used as the account key.

A stacks address can be created as follows

``` 
val privateKey = PrivateKey.fromString(key)
val address = Address.fromPubKeys(AddressVersion.TestnetSingleSig, AddressHashMode.SerializeP2PKH, 1, arrayOf(pubKey)),
``` 
### Create and Sign Transactions
Transactions are created with builder functions of the `Stacks` object
* `makeSTXTokenTransfer`
* `makeSmartContractDeploy`
* `makeContractCall`

The signed transactions can by created by calling the `serialize` function
```
val transaction = makeSTXTokenTransfer(..)
val signedTransaction = transaction.serialize().toNonPrefixHexString()
```
and broadcasted to the network by calling the `broadcast` function
```
val result = transaction.broadcast()
```

## Run Tests
```
./gradlew shared:testDebugUnitTest -Xopt-in=kotlin.RequiresOptIn
```

On non-Apple host machines, this runs the tests on Android only.

## Run Sample App
### Android app
The `app` folder contains  a simple app for transferring STX. 