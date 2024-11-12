package com.inswave.whive.headquater.domian;

import com.inswave.whive.headquater.enums.ClientMessageType;
import lombok.Data;

@Data
public class BaseMessage {
    private ClientMessageType MsgType;
}
