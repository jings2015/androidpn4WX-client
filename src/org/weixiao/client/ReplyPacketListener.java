package org.weixiao.client;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.weixiao.iq.ReplyResultIQ;

import android.content.Intent;
import android.util.Log;

public class ReplyPacketListener implements PacketListener {
	private static final String LOGTAG = LogUtil
			.makeLogTag(ReplyPacketListener.class);
	private final XmppManager xmppManager;
	public ReplyPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Log.d(LOGTAG, "ReplyPacketListener.processPacket()...");
		Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
		if (packet instanceof ReplyResultIQ) {
			ReplyResultIQ reply = (ReplyResultIQ) packet;
			String method = reply.getMethod();
			String ecode = reply.getEcode();
			String emsg = reply.getEmsg();
			
			Log.d(LOGTAG, "method=" + method);
			Log.d(LOGTAG, "ecode=" + ecode);
			Log.d(LOGTAG, "emsg=" + emsg);

			Intent intent = new Intent(Constants.getActionReply(method));
			intent.putExtra("method", method);
			intent.putExtra(Constants.REPLY_ECODE, ecode);
			intent.putExtra(Constants.REPLY_EMSG, emsg);
			
			xmppManager.getContext().sendBroadcast(intent);
			
			
		}
	}

}
