package me.whereareiam.socialismus.api.model.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatSenderRequirements {
	public int minOnline;
	public int symbolCountThreshold;
	public String usePermission = "";
	public List<String> worlds = new ArrayList<>();
}
