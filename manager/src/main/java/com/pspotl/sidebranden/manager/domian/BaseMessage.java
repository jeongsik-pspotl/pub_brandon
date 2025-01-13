package com.pspotl.sidebranden.manager.domian;

import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import lombok.Data;

@Data
public class BaseMessage {
    private ClientMessageType MsgType;
}
