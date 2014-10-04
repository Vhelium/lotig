package com.vhelium.lotig.components;

import java.util.Comparator;

public class TextButtonComparor implements Comparator<TextButton>
{
	@Override
	public int compare(TextButton cmd1, TextButton cmd2)
	{
		String s1 = cmd1.getText().toString();
		String s2 = cmd2.getText().toString();
		
		int res = String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		if(res == 0)
			res = s1.compareTo(s2);
		return res;
	}
}