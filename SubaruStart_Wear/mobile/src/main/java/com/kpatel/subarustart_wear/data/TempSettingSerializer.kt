package com.kpatel.subarustart_wear.data

import androidx.datastore.core.Serializer
import com.kpatel.subarustart_wear.TempSettingsStore
import java.io.InputStream
import java.io.OutputStream

object TempSettingSerializer : Serializer<TempSettingsStore> {
    override val defaultValue: TempSettingsStore = TempSettingsStore.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TempSettingsStore {
        return TempSettingsStore.parseFrom(input)
    }

    override suspend fun writeTo(t: TempSettingsStore, output: OutputStream) {
        t.writeTo(output)
    }
}