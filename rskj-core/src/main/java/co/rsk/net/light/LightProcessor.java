package co.rsk.net.light;

import co.rsk.net.BlockSyncService;
import co.rsk.net.MessageChannel;
import co.rsk.net.messages.BlockReceiptsResponseMessage;
import co.rsk.net.messages.Message;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.core.Block;
import org.ethereum.core.Blockchain;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.TransactionInfo;
import co.rsk.net.light.messages.TransactionIndexRequestMessage;
import co.rsk.net.light.messages.TransactionIndexResponseMessage;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.core.Block;
import org.ethereum.core.Blockchain;
import org.ethereum.core.Transaction;
import org.ethereum.db.ReceiptStore;
import org.ethereum.db.TransactionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.*;

public class LightProcessor {
    private static final Logger logger = LoggerFactory.getLogger("lightprocessor");
    // keep tabs on which nodes know which blocks.
    private final BlockSyncService blockSyncService;
    private final Blockchain blockchain;

    public LightProcessor(@Nonnull final Blockchain blockchain,
                          @Nonnull final BlockSyncService blockSyncService) {
        this.blockSyncService = blockSyncService;
        this.blockchain = blockchain;
    }

    /**
     * processBlockReceiptsRequest sends the requested block receipts if it is available.
     *
     * @param sender the sender of the BlockReceipts message.
     * @param requestId the id of the request
     * @param blockHash   the requested block hash.
     */
    public void processBlockReceiptsRequest(MessageChannel sender, long requestId, byte[] blockHash) {
        logger.trace("Processing block receipts request {} block {} from {}", requestId, Hex.toHexString(blockHash), sender.getPeerNodeID());
        final Block block = blockSyncService.getBlockFromStoreOrBlockchain(blockHash);

        if (block == null) {
            // Don't waste time sending an empty response.
            return;
        }

        List<TransactionReceipt> receipts = new LinkedList<>();

        for (Transaction tx :  block.getTransactionsList()) {
            TransactionInfo txInfo = blockchain.getTransactionInfo(tx.getHash().getBytes());
            receipts.add(txInfo.getReceipt());
        }

        Message responseMessage = new BlockReceiptsResponseMessage(requestId, receipts);
        sender.sendMessage(responseMessage);
    }

    public void processBlockReceiptsResponse(MessageChannel sender, BlockReceiptsResponseMessage message) {
        throw new UnsupportedOperationException();
    }

    public void processTransactionIndexRequestMessage(MessageChannel sender, TransactionIndexRequestMessage message) {
        logger.debug("transactionID request Message Recieved");
        byte[] hash = message.getHash();

        byte[] blockHash;
        byte[] blockNumber;
        byte[] txIndex;

        TransactionInfo txinfo = blockchain.getTransactionInfo(hash);

        if (txinfo == null) {
            byte[] empty = new byte[0];
            TransactionIndexResponseMessage response = new TransactionIndexResponseMessage(message.getId(),empty,empty,empty);
            sender.sendMessage(response);
            return;
        }

        blockHash = txinfo.getBlockHash();
        blockNumber = BigInteger.valueOf(blockchain.getBlockByHash(blockHash).getNumber()).toByteArray();
        txIndex = BigInteger.valueOf(txinfo.getIndex()).toByteArray();

        TransactionIndexResponseMessage response = new TransactionIndexResponseMessage(message.getId(),blockNumber,blockHash,txIndex);
        sender.sendMessage(response);
    }

    public void processTransactionIndexResponseMessage(MessageChannel sender, TransactionIndexResponseMessage message) {
        logger.debug("transactionIndex response Message Recieved");
        logger.debug("ID: "+message.getId());
        logger.debug("BlockHash: "+ Hex.toHexString(message.getBlockHash()));
        logger.debug("Blocknumber: "+Hex.toHexString(message.getBlockNumber()));
        logger.debug("TxIndex: "+Hex.toHexString(message.getTxIndex()));
    }
}
