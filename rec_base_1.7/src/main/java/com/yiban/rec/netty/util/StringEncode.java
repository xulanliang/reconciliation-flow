package com.yiban.rec.netty.util;

public enum StringEncode {
	UTF8 {
		public byte getValue() {
			return 1;
		}

		public String getEncoder() {
			return "UTF-8";
		}
	},
	GBK {
		public byte getValue() {
			return 2;
		}

		public String getEncoder() {
			return "GBK";
		}
	},
	GB2312 {
		public byte getValue() {
			return 3;
		}

		public String getEncoder() {
			return "gb2312";
		}
	},
	DEFAULT {
		public byte getValue() {
			return 1;
		}

		public String getEncoder() {
			return "UTF-8";
		}
	};

	public static StringEncode getEncodeMode(byte value) {
		StringEncode rslt = UTF8;
		switch (value) {
		case 1:
			rslt = UTF8;
			break;
		case 2:
			rslt = GBK;
			break;
		case 3:
			rslt = GB2312;
			break;
		default:
			rslt = UTF8;
			break;
		}
		return rslt;
	}

	public abstract byte getValue();

	public abstract String getEncoder();
}
