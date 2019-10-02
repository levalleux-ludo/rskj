/*
 * This file is part of RskJ
 * Copyright (C) 2018 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.rpc.modules.trace;

import com.fasterxml.jackson.annotation.JsonGetter;

public class TransactionTrace {
    private ActionTransactionTrace action;
    private String blockHash;
    private long blockNumber;
    private int subtraces;
    private int[] traceAddress;
    private String transactionHash;
    private int transactionPosition;
    private String type;

    public TransactionTrace(
            ActionTransactionTrace action,
            String blockHash,
            long blockNumber,
            String transactionHash,
            int transactionPosition,
            String type
    ) {
        this.action = action;
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.transactionHash = transactionHash;
        this.transactionPosition = transactionPosition;
        this.type = type;
    }

    @JsonGetter("action")
    public ActionTransactionTrace getAction() {
        return this.action;
    }

    @JsonGetter("blockHash")
    public String getBlockHash() {
        return this.blockHash;
    }

    @JsonGetter("blockNumber")
    public long getBlockNumber() {
        return this.blockNumber;
    }

    @JsonGetter("transactionHash")
    public String getTransactionHash() {
        return this.transactionHash;
    }

    @JsonGetter("transactionPosition")
    public int getTransactionPosition() {
        return this.transactionPosition;
    }

    @JsonGetter("type")
    public String getType() {
        return this.type;
    }
 }
