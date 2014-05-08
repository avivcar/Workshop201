package server.reactor;


import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import server.protocol.*;
import server.tokenizer.*;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask implements Runnable {

	private final ServerProtocol _protocol;
	private final MessageTokenizer _tokenizer;
	private final ConnectionHandler _handler;

	public ProtocolTask(final ServerProtocol protocol, final MessageTokenizer tokenizer, final ConnectionHandler h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
      // go over all complete messages and process them.
      while (_tokenizer.hasMessage()) {
         String msg = _tokenizer.nextMessage();
         String response = this._protocol.processMessage(msg);
         if (response != null) {
            try {
               ByteBuffer bytes = _tokenizer.getBytesForMessage(response);
               this._handler.addOutData(bytes);
            } catch (CharacterCodingException e) { e.printStackTrace(); }
         }
      }
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
