package cn.sz1727.core.i18n;

import java.io.Serializable;

/**
 * cn.sz1727.core.i18n.Message.java
 *
 * @author Dongd_Zhou
 * @creation 2014年8月19日
 * 
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 65144943423139239L;

	private String message;
	private String messageKey;
	private Object[] args;
	private Message nextMessage;

	protected Message(String message, String messageKey, Object... args) {
		super();
		this.message = message;
		this.messageKey = messageKey;
		this.args = args;
	}

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message + (nextMessage != null ? ". " + nextMessage.getMessage() : "");
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * @return the nextMessage
	 */
	public Message getNextMessage() {
		return nextMessage;
	}

	/**
	 * @param nextMessage
	 *            the nextMessage to set
	 */
	public Message setNextMessage(Message nextMessage) {
		this.nextMessage = nextMessage;
		return this;
	}

	@Override
	public String toString() {
		return this.getMessage();
	}
}
