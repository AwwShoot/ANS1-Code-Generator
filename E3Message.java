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
/* File E3Message.java                                                           */
/* CSE4232 Marius Silaghi 2017                                                   */
/* E3Message ::= [3] EXPLICIT SEQUENCE{version [APPLICATION 0] IMPLICIT OCTET_STRING, message UTF8STRING} */

import net.ddp2p.ASN1.*;


/* We decorate the class with a specifies of the ASN1 as [CONTEXT 3] to automate encoding/decoding */
@ASN1Type(_class=Encoder.CLASS_CONTEXT, _pc=Encoder.PC_CONSTRUCTED, _tag=3)
/* We extend the class ASNObjArrayable to be able to easily create arrays of E3Message objects */
class E3Message extends ASNObjArrayable {

  /* the next tag is needed for the tag 0 of the member "value".                 */
  final static byte TAG_AP0 = Encoder. buildASN1byteType(Encoder.CLASS_APPLICATION,0,(byte)0);
  /* the next tag can be used for the tag [3] of the sequence E3Message          */
  final static byte TAG_CC3 = Encoder. buildASN1byteType(Encoder.CLASS_CONTEXT, 
				           Encoder.PC_CONSTRUCTED, (byte)3);

  /* It is smart to version messages, since extensions may be needed in future */
  int version = 1;
  /* This is the payload of this simple message*/
  String message;

  /* Encoding is performed according to the ASN1 definition */
  public Encoder getEncoder() {
    /* Everything is in a SEQUENCE*/
    Encoder e = new Encoder().initSequence();
    /* To this sequence we add the version and message*/
    e.addToSequence(
	new Encoder(version)  /* The constructor creates tag INTEGER*/
	 .setASN1Type(TAG_AP0)); /* The tag is changed to AP0*/
    e.addToSequence(new Encoder(message)); /* Message added with default tag*/

    /* The tag in decorator is set manually because it is Explicit          */
    /* The decorator sets automatically implicit tags                       */
    return e.setASN1TypeExplicit(E3Message.class);

    /* tag could have also been set with: return e.setASN1TypeExplicit(TAG_CC3); */ 

  }

  /* Decoding performed for deserialization                   */
  public E3Message decode(Decoder dec) throws ASN1DecoderFail {
   /* Since message tag was explicit, it has to be matched for decoding*/
   Decoder d = dec.getContentExplicit();
   /* Elements are extracted in the order they were inserted, first the version */
   version = d.getFirstObject(true).getInteger(TAG_AP0).intValue();
   /* Check if you can support the obtained version */
   if (version > 1) {System.out.println("May need to upgrade!");} 
    /* Next extract the message */
   message = d.getFirstObject(true).getString();

   /* You need to decide how to react if the unsupported version has extra objects */
   /* Here we throw an exception ! */
   if (d.getTypeByte() != 0) throw new ASN1DecoderFail("Extra objects!");
   return this;
  }

  /* This is needed to support simple decoding of arrays           */
  public E3Message instance() throws CloneNotSupportedException {return new E3Message();}
}

