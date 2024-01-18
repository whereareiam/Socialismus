package me.whereareiam.socialismus.core.model.swapper;

import java.util.ArrayList;
import java.util.List;

public class Swapper {
	public boolean enabled = true;
	public List<String> content = new ArrayList<>();
	public List<String> contentHover = new ArrayList<>();
	public List<String> placeholders = new ArrayList<>();
	public SwapperSettings settings = new SwapperSettings();
	public SwapperRequirements requirements = new SwapperRequirements();
}
