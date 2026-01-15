package com.wonddak.loacell.storage

expect class CommonStorageReference

expect fun CommonStorageReference.getChildPath(path: String): CommonStorageReference

expect fun CommonStorageReference.downloadToFile(
    path: String,
    success: () -> Unit,
    fail: (Error) -> Unit
): CommonStorageReference

expect fun CommonStorageReference.getDownloadUrl(
    success: (String) -> Unit,
)