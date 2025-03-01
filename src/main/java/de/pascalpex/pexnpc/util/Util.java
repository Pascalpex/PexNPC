package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;

public class Util {

    public static boolean checkName(String name) {
        String prename = name.length() > 16 ? name.substring(0, 16) : name;
        for (NPC npc : NPCData.getAllNpcs()) {
            String checkName = npc.getName();
            checkName = checkName.length() > 16 ? checkName.substring(0, 16) : checkName;
            if (checkName.equals(prename)) {
                return false;
            }
        }
        return true;
    }

}
