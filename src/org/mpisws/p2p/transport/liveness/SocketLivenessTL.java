/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

*******************************************************************************/ 
package org.mpisws.p2p.transport.liveness;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mpisws.p2p.transport.ErrorHandler;
import org.mpisws.p2p.transport.MessageCallback;
import org.mpisws.p2p.transport.MessageRequestHandle;
import org.mpisws.p2p.transport.P2PSocket;
import org.mpisws.p2p.transport.SocketCallback;
import org.mpisws.p2p.transport.SocketRequestHandle;
import org.mpisws.p2p.transport.TransportLayer;
import org.mpisws.p2p.transport.TransportLayerCallback;
import org.mpisws.p2p.transport.liveness.LivenessListener;
import org.mpisws.p2p.transport.liveness.LivenessProvider;
import org.mpisws.p2p.transport.util.SocketRequestHandleImpl;

import rice.environment.Environment;
import rice.environment.logging.Logger;

/**
 * Returns alive if there is a socket.
 * 
 * TODO: record how long it was since it last wrote, and call it dead earlier than 15 mins
 * 
 * @author Jeff Hoye
 *
 */
public class SocketLivenessTL<Identifier> implements 
    LivenessProvider<Identifier>, 
    TransportLayer<Identifier, ByteBuffer>, 
    TransportLayerCallback<Identifier, ByteBuffer> {
  /**
   * Our sockets for the nodes.  Used to determine liveness.
   */
  protected Map<Identifier, P2PSocket<Identifier>> sockets = new HashMap<Identifier, P2PSocket<Identifier>>();
  protected Collection<LivenessListener<Identifier>> livenessListeners = new ArrayList<LivenessListener<Identifier>>();
  protected TransportLayer<Identifier, ByteBuffer> tl;
  protected TransportLayerCallback<Identifier, ByteBuffer> callback;
  protected Logger logger;
  
  public SocketLivenessTL(TransportLayer<Identifier, ByteBuffer> tl, Environment environment) {
    this.tl = tl;
    this.logger = environment.getLogManager().getLogger(SocketLivenessTL.class, null);
    tl.setCallback(this);
  }

  public void addLivenessListener(LivenessListener<Identifier> name) {
    livenessListeners.add(name);
  }

  public boolean removeLivenessListener(LivenessListener<Identifier> name) {
    return livenessListeners.remove(name);
  }

  public boolean checkLiveness(Identifier i, Map<String, Object> options) {
    return false;
  }

  public void clearState(Identifier i) {
    throw new RuntimeException("Not implemented.");
  }

  public int getLiveness(Identifier i, Map<String, Object> options) {
    if (sockets.containsKey(i)) return LIVENESS_ALIVE;
    return LIVENESS_DEAD;
  }

  public void acceptMessages(boolean b) {
    tl.acceptMessages(b);
  }

  public void acceptSockets(boolean b) {
    tl.acceptSockets(b);
  }

  public Identifier getLocalIdentifier() {
    throw new RuntimeException("Not implemented.");
  }

  public SocketRequestHandle<Identifier> openSocket(Identifier i,
      SocketCallback<Identifier> deliverSocketToMe, Map<String, Object> options) {
//    throw new RuntimeException("Not implemented.");
    SocketRequestHandleImpl ret = new SocketRequestHandleImpl(i, options, logger);
    deliverSocketToMe.receiveException(ret, new IOException("Not Implemented."));
    return ret;
  }

  /**
   * this is UDP
   */
  public MessageRequestHandle<Identifier, ByteBuffer> sendMessage(Identifier i,
      ByteBuffer m, MessageCallback<Identifier, ByteBuffer> deliverAckToMe,
      Map<String, Object> options) {
//    logger.log("sendMessage()");
    return tl.sendMessage(i, m, deliverAckToMe, options);
  }

  public void setCallback(TransportLayerCallback<Identifier, ByteBuffer> callback) {
    this.callback = callback;
  }

  public void setErrorHandler(ErrorHandler<Identifier> handler) {
    // TODO Auto-generated method stub
    
  }

  public void destroy() {
    tl.destroy();
  }

  public void incomingSocket(P2PSocket<Identifier> s) throws IOException {
    sockets.put(s.getIdentifier(), s);
    callback.incomingSocket(s);
  }

  public void messageReceived(Identifier i, ByteBuffer m, Map<String, Object> options)
      throws IOException {
//    logger.log("messageReceived()");
    callback.messageReceived(i, m, options);
  }


}
