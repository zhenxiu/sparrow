package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Map;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheOrderSet {

    Long getSize(KEY key) throws CacheConnectionException;

    Long add(KEY key, Object value, Long score) throws CacheConnectionException;

    Long remove(KEY key, Object value) throws CacheConnectionException;

    Long remove(KEY key, Long from, Long to) throws CacheConnectionException;

    Double getScore(KEY key, Object value) throws CacheConnectionException;

    Long getRank(KEY key, Object value) throws CacheConnectionException;

    Map<String, Double> getAllWithScore(KEY key) throws CacheConnectionException;

    Integer putAllWithScore(KEY key, Map<String, Double> keyScoreMap) throws CacheConnectionException;

    Map<String, Double> getAllWithScore(KEY key, CacheDataNotFound<Map<String,Double>> hook);
}
