/* ------------------------------------------------------------------------- */
/*   Copyright (C) 2017 Marius C. Silaghi
		Author: Marius Silaghi: msilaghi@fit.edu
		Florida Tech, Human Decision Support Systems Laboratory
   
       This program is free software; you can redistribute it and/or modify
       it under the terms of the GNU Affero General Public License as published by
       the Free Software Foundation; either the current version of the License, or
       (at your option) any later version.
   
      This program is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU General Public License for more details.
  
      You should have received a copy of the GNU Affero General Public License
      along with this program; if not, write to the Free Software
      Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.              */
/* ------------------------------------------------------------------------- */


import net.ddp2p.ASN1.*;

// I2Message ::= [CONTEXT 2] IMPLICIT SEQUENCE{value [APPLICATION 0] IMPLICIT INTEGER, message UTF8STRING}
@ASN1Type(_class=Encoder.CLASS_CONTEXT, _pc=Encoder.PC_CONSTRUCTED, _tag=2)
class I2Message extends ASNObjArrayable {
  // the next tag is needed because of the for the tag 0 of the member "value".
  final static byte TAG_AP0 = Encoder. buildASN1byteType(Encoder.CLASS_APPLICATION, Encoder.PC_PRIMITIVE, (byte)0);
  // the next tag could be used for the tag [CONTEXT 2] of the sequence I2Message
  final static byte TAG_CC2 = Encoder. buildASN1byteType(Encoder.CLASS_CONTEXT, 
				         	  Encoder.PC_CONSTRUCTED, (byte)2);
  int value = 2;
  String message;
  public Encoder getEncoder() {
		Encoder e = new Encoder().initSequence();
		e.addToSequence(
			new Encoder(value)
			 .setASN1Type(TAG_AP0));
		e.addToSequence(new Encoder(message));
		//  return e.setASN1TypeImplicit(TAG_CC2);
		return e.setASN1TypeImplicit(I2Message.class);
}
  public I2Message decode(Decoder dec) throws ASN1DecoderFail {
		Decoder d = dec.getContentImplicit();
		value = d.getFirstObject(true).getInteger(TAG_AP0).intValue();
		message = d.getFirstObject(true).getString();
		if (d.getTypeByte() != 0) throw new ASN1DecoderFail("Extra objects!");
		return this;
  }
  public I2Message instance() throws CloneNotSupportedException {return new I2Message();}
}

