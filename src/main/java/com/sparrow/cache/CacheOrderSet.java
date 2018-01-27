package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Map;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheOrderSet {

    Long getSize(KEY key) throws CacheConnectionException;

    <T> Long add(KEY key, T value, double score) throws CacheConnectionException;

    <T> Long remove(KEY key, T value) throws CacheConnectionException;

    Long remove(KEY key, Long from, Long to) throws CacheConnectionException;

    <T> Double getScore(KEY key, T value) throws CacheConnectionException;

    <T> Long getRank(KEY key, T value) throws CacheConnectionException;

    <T> Map<T, Double> getAllWithScore(KEY key) throws CacheConnectionException;

    <T> Integer putAllWithScore(KEY key, Map<T, Double> keyScoreMap) throws CacheConnectionException;

    <T> Map<T, Double> getAllWithScore(KEY key, Class keyClazz, CacheDataNotFound<Map<T, Double>> hook);
}
