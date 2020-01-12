package com.transit.domain.mta.bus;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class MetaDataResponse<T> {
    private int code;
    private long currentTime;
    private T data;

    public static class ListResponse<T> {
        @JsonAlias({"routes", "stops", "list"})
        private List<T> list;

        public List<T> getList() {
            return list;
        }

        public ListResponse setList(List<T> list) {
            this.list = list;
            return this;
        }
    }

    public int getCode() {
        return code;
    }

    public MetaDataResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public MetaDataResponse<T> setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
        return this;
    }

    public T getData() {
        return data;
    }

    public MetaDataResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
