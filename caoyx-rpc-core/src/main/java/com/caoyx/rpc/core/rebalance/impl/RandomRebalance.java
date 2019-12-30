package com.caoyx.rpc.core.rebalance.impl;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.rebalance.Rebalance;

import java.util.List;
import java.util.Random;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 14:47
 */
public class RandomRebalance implements Rebalance {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public Address rebalance(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return addresses.get(random.nextInt(addresses.size()));
    }
}