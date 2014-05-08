package server.reactor;

import java.util.concurrent.ExecutorService;
import java.nio.channels.Selector;

import server.protocol.*;
import server.tokenizer.*;

/**
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData {

    private final ExecutorService _executor;
    private final Selector _selector;
    private final ServerProtocolFactory _protocolMaker;
    private final TokenizerFactory _tokenizerMaker;
    
    public ExecutorService getExecutor() {
        return _executor;
    }

    public Selector getSelector() {
        return _selector;
    }

	public ReactorData(ExecutorService _executor, Selector _selector, ServerProtocolFactory protocol, TokenizerFactory tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public ServerProtocolFactory getProtocolMaker() {
		return _protocolMaker;
	}

	public TokenizerFactory getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
