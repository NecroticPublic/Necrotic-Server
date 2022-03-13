package com.ruse.model.definitions;

import com.ruse.world.clip.stream.ByteStreamExt;
import com.ruse.world.clip.stream.MemoryArchive;


// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class GameObjectDefinition {
	
	public static GameObjectDefinition class46;
	
	public boolean rangableObject() {
		int[] rangableObjects = {3007, 980, 4262, 14437, 14438, 4437, 4439, 3487, 3457};
		for (int i : rangableObjects) {
			if (i == id) {
				return true;
			}
		}
		if (name != null) {
			final String name1 = name.toLowerCase();
			String[] rangables = {"altar", "pew", "log", "stump", "stool", "sign", "cart", "chest", "rock", "bush", "hedge", "chair", "table", "crate", "barrel", "box", "skeleton", "corpse", "vent", "stone", "rockslide"};
			for (String i : rangables) {
				if (name1.contains(i)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean removedObject(int id) {
		return id == 11328 || id == 2956 || id == 463 || id == 462 || id == 25026 || id == 25020 || id == 25019 || id == 25024 || id == 25025 || id == 25016 || id == 10527 || id == 10529 || id == 40257 || id == 296 || id == 300 || id == 1747 || id == 7332 || id == 7326 || id == 7325 || id == 7385 || id == 7331 || id == 7385 || id == 7320 || id == 7317 || id == 7323 || id == 7354 || id == 1536 || id == 1537 || id == 5126 || id == 1551 || id == 1553 || id == 1516 || id == 1519 || id == 1557 || id == 1558 || id == 7126 || id == 733 || id == 14233 || id == 14235 || id == 1596 || id == 1597 || id == 14751 || id == 14752 || id == 14923 || id == 36844 || id == 30864 || id == 2514 || id == 1805 || id == 15536 || id == 2399 || id == 14749 || id == 29315 || id == 29316 || id == 29319 || id == 29320 || id == 29360 || id == 1528 || id == 36913 || id == 36915 || id == 15516 ||id == 35549 || id == 35551 || id == 26808 || id == 26910 || id == 26913 || id == 24381 || id == 15514 || id == 25891 || id == 26082 || id == 26081 || id == 1530 || id == 16776 || id == 16778 || id == 28589 || id == 1533 || id == 17089 || id == 1600 || id == 1601 || id == 11707 || id == 24376 || id == 24378 || id == 40108 || id == 59 || id == 2069 || id == 36846 || id == 1508 || id == 1506 || id == 4031;	
	}
	
	private static GameObjectDefinition forId667(int id) {
		if (id > totalObjects667 || id > streamIndices667.length - 1) {
			id = 0;
		}
		for(int j = 0; j < 20; j++) {
			if(cache[j].id == id) {
				return cache[j];
			}
		}
		cacheIndex = (cacheIndex + 1) % 20;
		GameObjectDefinition object = cache[cacheIndex];
		/*Removing doors etc*/
		if(removedObject(id)) {
			object.unwalkable = false;
			return object;
		}
		dataBuffer667.currentOffset = streamIndices667[id];
		object.id = id;
		object.nullLoader();
		try {
		object.readValues667(dataBuffer667);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public static GameObjectDefinition forId(int i) {
		if (i > streamIndices525.length) {
			return forId667(i);
		}
		
		for (int j = 0; j < 20; j++) {
			if (cache[j].id == i) {
				return cache[j];
			}
		}
		
		cacheIndex = (cacheIndex + 1) % 20;
		class46 = cache[cacheIndex];
		
		if (i > streamIndices525.length - 1 || i < 0) {
			return null;
		}
		
		dataBuffer525.currentOffset = streamIndices525[i];
		/*Removing doors etc*/
		boolean removeObject = i == 7332 || i == 7326 || i == 7325 || i == 7385 || i == 7331 || i == 7385 || i == 7320 || i == 7317 || i == 7323 || i == 7354 || i == 1536 || i == 1537 || i == 5126 || i == 1551 || i == 1553 || i == 1516 || i == 1519 || i == 1557 || i == 1558 || i == 7126 || i == 733 || i == 14233 || i == 14235 || i == 1596 || i == 1597 || i == 14751 || i == 14752 || i == 14923 || i == 36844 || i == 30864 || i == 2514 || i == 1805 || i == 15536 || i == 2399 || i == 14749 || i == 29315 || i == 29316 || i == 29319 || i == 29320 || i == 29360 || i == 1528 || i == 36913 || i == 36915 || i == 15516 ||i == 35549 || i == 35551 || i == 26808 || i == 26910 || i == 26913 || i == 24381 || i == 15514 || i == 25891 || i == 26082 || i == 26081 || i == 1530 || i == 16776 || i == 16778 || i == 28589 || i == 1533 || i == 17089 || i == 1600 || i == 1601 || i == 11707 || i == 24376 || i == 24378 || i == 40108 || i == 59 || i == 2069 || i == 36846 || i == 1508 || i == 1506 || i == 4031;
		if(removeObject) {
			class46.unwalkable = false;
			return class46;
		}
		class46.id = i;
		class46.nullLoader();
		class46.readValues(dataBuffer525);
		return class46;
	}
	
	   public int[] solidObjects = {1902,1903,1904,1905,
			   1906,1907,1908,1909,1910,1911,1912,1536,1535,1537,1538,5139,5140,5141,5142,5143,5144,5145,5146,5147,5148,5149,5150,
			   1534,1533,1532,1531,1530,1631,1624,733,1658,1659,1631,1620,14723,14724,14726,14622,14625,14627,11668,11667,
			   14543,14749,14561,14750,14752,14751,1547,1548,1415,1508,1506,1507,1509,1510,1511,1512,1513,1514,1515,1516,
			   1517,1518,1519,1520,1521,1522,1523,1524,1525,1526,1527,1528,1529,1505,1504,3155,3154,3152,10748,9153,9154,
			   9473,1602,1603,1601,1600,9544,9563,9547,2724,6966,6965,9587,9588,9626,9627,9596,9598,11712,11713,11773,11776,
			   11652,11818,11716,11721,14409,11715,11714,11825,11409,11826,11819,14411,14410,11719,11717,14402,11828,11772,
			   11775,11686,12278,1853,11611,11610,11609,11608,11607,11561,11562,11563,11564,11558,11616,11617,11625,11624,12990,
			   12991,5634,1769,1770,135,134,11536,11512,11529,11513,11521,11520,11519,11518,11517,11516,11514,11509,11538,11537,
			   11470,11471,136,11528,11529,11530,11531,1854,1000,9265,9264,1591,11708,11709,11851};
			       public void setSolid(int type) {
			   	aBoolean779 = false;
			   		for(int i = 0; i < solidObjects.length;i++) {
			   			if(type == solidObjects[i]) {
			   				unwalkable = true;
			   				aBoolean779 = true;
			   				continue;
			   			}
			   		}

			       }

	public void nullLoader() {
		modelArray = null;
		objectModelType = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		tileSizeX = 1;
		tileSizeY = 1;
		unwalkable = true;
		impenetrable = true;
		interactive = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}
	
	private static ByteStreamExt dataBuffer525;
	private static ByteStreamExt dataBuffer667;
	
	public static int totalObjects667;

	public static void init() {
		//long startup = System.currentTimeMillis();
		//System.out.println("Loading cache game object definitions...");
		
		dataBuffer525 = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt idxBuffer525 = new ByteStreamExt(getBuffer("loc.idx"));
		
		dataBuffer667 = new ByteStreamExt(getBuffer("667loc.dat"));
		ByteStreamExt idxBuffer667 = new ByteStreamExt(getBuffer("667loc.idx"));
		
		int totalObjects525 = idxBuffer525.readUnsignedWord();
		totalObjects667 = idxBuffer667.readUnsignedWord();
				
		streamIndices525 = new int[totalObjects525];
		int i = 2;
		for (int j = 0; j < totalObjects525; j++) {
			streamIndices525[j] = i;
			i += idxBuffer525.readUnsignedWord();
		}
		
		streamIndices667 = new int[totalObjects667];
		
		i = 2;
		for (int j = 0; j < totalObjects667; j++) {
			streamIndices667[j] = i;
			i += idxBuffer667.readUnsignedWord();
		}
		
		cache = new GameObjectDefinition[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new GameObjectDefinition();
		}
		
		///System.out.println("Loaded " + totalObjects525 + " cache object definitions #525 and " 
		//		+ totalObjects667 + " cache object definitions #667 in " + (System.currentTimeMillis() - startup) + "ms");
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File("./data/clipping/objects/" + s);
			if (!f.exists())
				return null;
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void readValues(ByteStreamExt buffer) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = buffer.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int k = buffer.readUnsignedByte();
					if (k > 0)
						if (modelArray == null || lowMem) {
							objectModelType = new int[k];
							modelArray = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								modelArray[k1] = buffer.readUnsignedWord();
								objectModelType[k1] = buffer.readUnsignedByte();
							}
						} else {
							buffer.currentOffset += k * 3;
						}
				} else if (opcode == 2)
					name = buffer.readString();
				else if (opcode == 3)
					description = buffer.readBytes();
				else if (opcode == 5) {
					int l = buffer.readUnsignedByte();
					if (l > 0)
						if (modelArray == null || lowMem) {
							objectModelType = null;
							modelArray = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								modelArray[l1] = buffer.readUnsignedWord();
						} else {
							;// buffer.currentOffset += l * 2;
						}
				} else if (opcode == 14)
					tileSizeX = buffer.readUnsignedByte();
				else if (opcode == 15)
					tileSizeY = buffer.readUnsignedByte();
				else if (opcode == 17)
					unwalkable = false;
				else if (opcode == 18)
					impenetrable = false;
				else if (opcode == 19) {
					i = buffer.readUnsignedByte();
					if (i == 1)
						interactive = true;
				} else if (opcode == 21)
					aBoolean762 = true;
				else if (opcode == 22)
					aBoolean769 = false;//
				else if (opcode == 23)
					aBoolean764 = true;
				else if (opcode == 24) {
					anInt781 = buffer.readUnsignedWord();
					if (anInt781 == 65535)
						anInt781 = -1;
				} else if (opcode == 28)
					anInt775 = buffer.readUnsignedByte();
				else if (opcode == 29)
					aByte737 = buffer.readSignedByte();
				else if (opcode == 39)
					aByte742 = buffer.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (actions == null)
						actions = new String[10];
					actions[opcode - 30] = buffer.readString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden"))
						actions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = buffer.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = buffer.readUnsignedWord();
						originalModelColors[i2] = buffer.readUnsignedWord();
					}
				} else if (opcode == 60)
					anInt746 = buffer.readUnsignedWord();
				else if (opcode == 62)
					aBoolean751 = true;
				else if (opcode == 64)
					aBoolean779 = false;
				else if (opcode == 65)
					anInt748 = buffer.readUnsignedWord();
				else if (opcode == 66)
					anInt772 = buffer.readUnsignedWord();
				else if (opcode == 67)
					anInt740 = buffer.readUnsignedWord();
				else if (opcode == 68)
					anInt758 = buffer.readUnsignedWord();
				else if (opcode == 69)
					anInt768 = buffer.readUnsignedByte();
				else if (opcode == 70)
					anInt738 = buffer.readSignedWord();
				else if (opcode == 71)
					anInt745 = buffer.readSignedWord();
				else if (opcode == 72)
					anInt783 = buffer.readSignedWord();
				else if (opcode == 73)
					aBoolean736 = true;
				else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75)
						continue;
					anInt760 = buffer.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
			anInt774 = buffer.readUnsignedWord();
			if (anInt774 == 65535)
				anInt774 = -1;
			anInt749 = buffer.readUnsignedWord();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = buffer.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = buffer.readUnsignedWord();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			interactive = modelArray != null
					&& (objectModelType == null || objectModelType[0] == 10);
			if (actions != null)
				interactive = true;
		}
		if (aBoolean766) {
			unwalkable = false;
			impenetrable = false;
		}
		if (anInt760 == -1)
			anInt760 = unwalkable ? 1 : 0;
	}
	
	private void readValues667(ByteStreamExt buffer) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = buffer.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int k = buffer.readUnsignedByte();
					if (k > 0)
						if (modelArray == null || lowMem) {
							objectModelType = new int[k];
							modelArray = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								modelArray[k1] = buffer.readUnsignedWord();
								objectModelType[k1] = buffer.readUnsignedByte();
							}
						} else {
							buffer.currentOffset += k * 3;
						}
				} else if (opcode == 2)
					name = buffer.readString();
				else if (opcode == 3)
					description = buffer.readBytes();
				else if (opcode == 5) {
					int l = buffer.readUnsignedByte();
					if (l > 0)
						if (modelArray == null || lowMem) {
							objectModelType = null;
							modelArray = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								modelArray[l1] = buffer.readUnsignedWord();
						} else {
							;//buffer.offset += l * 2;
						}
				} else if (opcode == 14)
					tileSizeX = buffer.readUnsignedByte();
				else if (opcode == 15)
					tileSizeY = buffer.readUnsignedByte();
				else if (opcode == 17)
					unwalkable = false;
				else if (opcode == 18)
					impenetrable = false;
				else if (opcode == 19) {
					i = buffer.readUnsignedByte();
					if (i == 1)
						interactive = true;
				} else if (opcode == 21) {
					//conformable = true;
				} else if (opcode == 22) {
					//blackModel = false;//
				} else if (opcode == 23)
					aBoolean764 = true;
				else if (opcode == 24) {
					buffer.readUnsignedWord();
				} else if (opcode == 28)
					buffer.readUnsignedByte();
				else if (opcode == 29)
					aByte737 = buffer.readSignedByte();
				else if (opcode == 39)
					aByte742 = buffer.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (actions == null)
						actions = new String[10];
					actions[opcode - 30] = buffer.readString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden")
							|| actions[opcode - 30].equalsIgnoreCase("null"))
						actions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = buffer.readUnsignedByte();
					for (int i2 = 0; i2 < i1; i2++) {
						buffer.readUnsignedWord();
						buffer.readUnsignedWord();
					}
				} else if (opcode == 60)
					buffer.readUnsignedWord();
				else if (opcode == 62)
					aBoolean751 = true;
				else if (opcode == 64)
					aBoolean779 = false;
				else if (opcode == 65)
					anInt748 = buffer.readUnsignedWord();
				else if (opcode == 66)
					anInt772 = buffer.readUnsignedWord();
				else if (opcode == 67)
					anInt740 = buffer.readUnsignedWord();
				else if (opcode == 68)
					buffer.readUnsignedWord();
				else if (opcode == 69)
					anInt768 = buffer.readUnsignedByte();
				else if (opcode == 70)
					anInt738 = buffer.readSignedWord();
				else if (opcode == 71)
					anInt745 = buffer.readSignedWord();
				else if (opcode == 72)
					anInt783 = buffer.readSignedWord();
				else if (opcode == 73)
					aBoolean736 = true;
				else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75)
						continue;
					anInt760 = buffer.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
				anInt774 = buffer.readUnsignedWord();
			if (anInt774 == 65535)
				anInt774 = -1;
				anInt749 = buffer.readUnsignedWord();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = buffer.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = buffer.readUnsignedWord();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			interactive = modelArray != null && (objectModelType == null || objectModelType[0] == 10);
			if (actions != null)
				interactive = true;
		}
		if (aBoolean766) {
			unwalkable = false;
			impenetrable = false;
		}
		if (anInt760 == -1)
			anInt760 = unwalkable ? 1 : 0;
    }

	public GameObjectDefinition() {
		id = -1;
	}

	public boolean hasActions() {
		return interactive;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public int xLength() {
		return tileSizeX;
	}

	public int yLength() {
		return tileSizeY;
	}

	public boolean aBoolean736;
	public byte aByte737;
	public int anInt738;
	public String name;
	public int anInt740;
	public byte aByte742;
	public int tileSizeX;
	public int anInt745;
	public int anInt746;
	public int[] originalModelColors;
	public int anInt748;
	public int anInt749;
	public boolean aBoolean751;
	public static boolean lowMem;
	public int id;
	public static int[] streamIndices525;
	public static int[] streamIndices667;
	public boolean impenetrable;
	public int anInt758;
	public int childrenIDs[];
	public int anInt760;
	public int tileSizeY;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean766;
	public boolean unwalkable;
	public int anInt768;
	public boolean aBoolean769;
	public static int cacheIndex;
	public int anInt772;
	public int[] modelArray;
	public int anInt774;
	public int anInt775;
	public int[] objectModelType;
	public byte description[];
	public boolean interactive;
	public boolean aBoolean779;
	public int anInt781;
	public static GameObjectDefinition[] cache;
	public int anInt783;
	public int[] modifiedModelColors;
	public String actions[];
	public static MemoryArchive archive;

	public int actionCount() {
		return interactive ? 1 : 0;
	}

	public String getName() {
		return name;
	}
	
	public int getSizeX() {
		return tileSizeX;
	}
	
	public int getSizeY() {
		return tileSizeY;
	}

}
