package com.wonddak.loacell.storage

expect class CommonFireStorage

//FireStorage를 가져온다
expect fun getFireStorage(): CommonFireStorage

//FireStorage로부터 Reference를 가져온다.
expect fun CommonFireStorage.getCommonReference() : CommonStorageReference