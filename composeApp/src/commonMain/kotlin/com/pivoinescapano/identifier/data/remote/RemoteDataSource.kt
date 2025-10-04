package com.pivoinescapano.identifier.data.remote

import com.pivoinescapano.identifier.data.model.FieldEntry

interface RemoteDataSource {
    suspend fun fetchAllFieldData(): NetworkResult<List<FieldEntry>>
}
