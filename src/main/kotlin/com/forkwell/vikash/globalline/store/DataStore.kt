package com.forkwell.vikash.globalline.store

class DataStore {

    val dataStoreMap: MutableMap<String, String> = HashMap<String, String>()

    // put and return if value has been updated
    fun putVal(key: String, newValue: String): Boolean {
        var oldValue = dataStoreMap.get(key);
        dataStoreMap.put(key, newValue);
        return (oldValue == null || oldValue.equals(newValue))
    }

    fun isEmpty(): Boolean {
        return dataStoreMap.isEmpty()
    }

    fun size(): Int {
        return dataStoreMap.size
    }

    fun clear() {
        dataStoreMap.clear()
    }
}