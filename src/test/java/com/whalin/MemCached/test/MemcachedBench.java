/**
 * Copyright (c) 2008 Greg Whalin
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the BSD license
 *
 * This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * You should have received a copy of the BSD License along with this
 * library.
 *
 * @author Greg Whalin <greg@meetup.com> 
 */
package com.whalin.MemCached.test;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemcachedBench {

	public static void main(String[] args) {

		int runs = Integer.parseInt(args[0]);

		String[] serverlist = { "localhost:11211" };

		// initialize the pool for memcache servers
		SockIOPool pool = SockIOPool.getInstance("test");
		pool.setServers(serverlist);

		pool.setInitConn(2);
		pool.setMinConn(2);
		pool.setMaxConn(4);
		// pool.setMaintSleep( 20 );

		pool.setNagle(false);
		pool.initialize();

		// get client instance
		MemCachedClient mc = new MemCachedClient("test");

		String keyBase = "testKey";
		String object = "This is a test of an object blah blah es, serialization does not seem to slow things down so much.  The gzip compression is horrible horrible performance, so we only use it for very large objects.  I have not done any heavy benchmarking recently";

		long begin = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			mc.set(keyBase + i, object);
		}
		long end = System.currentTimeMillis();
		long time = end - begin;
		System.out.println(runs + " sets: " + time + "ms");

		begin = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			mc.get(keyBase + i);
		}
		end = System.currentTimeMillis();
		time = end - begin;
		System.out.println(runs + " gets: " + time + "ms");

		String[] keys = new String[runs];
		int j = 0;
		for (int i = 0; i < runs; i++) {
			keys[j] = keyBase + i;
			j++;
		}
		begin = System.currentTimeMillis();
		mc.getMulti(keys);
		end = System.currentTimeMillis();
		time = end - begin;
		System.out.println(runs + " getMulti: " + time + "ms");

		begin = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			mc.delete(keyBase + i);
		}
		end = System.currentTimeMillis();
		time = end - begin;
		System.out.println(runs + " deletes: " + time + "ms");

		SockIOPool.getInstance("test").shutDown();
	}
}
