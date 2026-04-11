package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;

public class Util {

    public static boolean isNameInvalid(String name, long targetId) {
        String prename = name.length() > 16 ? name.substring(0, 16) : name;
        for (NPC npc : NPCData.getAllNpcs()) {
            if(npc.getId() == targetId) {
                continue;
            }
            String checkName = npc.getName();
            checkName = checkName.length() > 16 ? checkName.substring(0, 16) : checkName;
            if (checkName.equals(prename)) {
                return true;
            }
        }
        return false;
    }

}
