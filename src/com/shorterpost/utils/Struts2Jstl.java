package io.techtx.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class Struts2Jstl {

	static String path = "";				 	//w - write
	static boolean write = true;             	//h - hidden
	static boolean hidden = true;            	//e - empty
	static boolean empty = true;             	//n - not empty
	static boolean notempty = true;          	//q - equal
	static boolean equal = true;             	//o - not equal
	static boolean notequal = true;          	//p  - present
	static boolean present = true;           	//s - select
	static boolean select = true;            	//t - text
	static boolean text = true;              	//m - multibox
	static boolean multibox = true;				//i - iterate 
	static boolean iterate = true;

	public static void main(String[] args)
	{

		if(args.length == 0)
		{
			System.out.println("please provide file name with -f filename");
			System.out.println("Help:::::::://w - write    \r\n" + 
					"//h - hidden   \r\n" + 
					"//e - empty    \r\n" + 
					"//n - not empty\r\n" + 
					"//q - equal    \r\n" + 
					"//o - not equal\r\n" + 
					"//p  - present \r\n" + 
					"//s - select   \r\n" + 
					"//t - text     \r\n" + 
					"//m - multibox \r\n" + 
					"//i - iterate  ");
			return;

		}
		if(args.length < 2)
		{
			System.out.println("please provide file name with -f filename");
			return;
		}


		if(args[0].equals("-f"))
		{
			path = args[1];
			if(isEmptyOrNull(path))
			{
				System.out.println("please provide file name with -f filename");
				return;}

		}

		if(args.length == 2)
		{
			convertStrutsTagToJstl();
			return;
		}

		if(!args[2].contains("w"))
		{
			write = false;
		}
		if(!args[2].contains("h"))
		{
			hidden = false;
		}
		if(!args[2].contains("e"))
		{
			empty = false;
		}
		if(!args[2].contains("n"))
		{
			notempty = false;
		}
		if(!args[2].contains("q"))
		{
			equal = false;
		}
		if(!args[2].contains("o"))
		{
			notequal = false;
		}
		if(!args[2].contains("p"))
		{
			present = false;
		}
		if(!args[2].contains("t"))
		{
			text = false;
		}
		if(!args[2].contains("m"))
		{
			multibox = false;
		}
		if(!args[2].contains("s"))
		{
			select = false;
		}
		if(!args[2].contains("i"))
		{
			iterate = false;
		}

		convertStrutsTagToJstl();
	}

	private static void convertStrutsTagToJstl()
	{

		try
		{
			String content  = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
			Parser parser = Parser.htmlParser();
			parser.settings(new ParseSettings(true, true)); // tag, attribute preserve case
			Document doc = Jsoup.parse(content,"", parser);

			Document.OutputSettings settings = doc.outputSettings();

			settings.prettyPrint(false);
			settings.escapeMode(Entities.EscapeMode.extended);
			//settings.charset("ASCII");

			Elements allElements = doc.getAllElements();
			for (Element element : allElements) {

				if(write && element.tagName().equals("bean:write"))
				{
					System.out.println("Processing bean:write");
					processBeanWriteTag(element);

				}
				else if(hidden && element.tagName().equals("html:hidden"))
				{
					System.out.println("Processing html:hidden");
					processHtmlHiddenTag(element);
				}
				else if(equal && element.tagName().equals("logic:equal"))
				{
					System.out.println("Processing logic:equal");
					processLogicEqualTag(element);
				}
				else if(notequal && element.tagName().equals("logic:notEqual"))
				{
					System.out.println("Processing logic:notEqual");
					processLogicNotEqualTag(element);
				}
				else if(present && element.tagName().equals("logic:present"))
				{
					System.out.println("Processing logic:present");
					processLogicPresentTag(element);
				}
				else if(empty && element.tagName().equals("logic:empty"))
				{
					System.out.println("Processing logic:empty");
					processLogicEmtpyTag(element);
				}
				else if(notempty && element.tagName().equals("logic:notEmpty"))
				{
					System.out.println("Processing logic:notEmpty");
					processLogicNotEmtpyTag(element);
				}
				else if(select && (element.tagName().equals("html:select") || 
						element.tagName().equals("html:option")  || 
						element.tagName().equals("html:options") ))
				{
					System.out.println("Processing html:select or html:option or html:options");
					processHtmlSelectNoptionTag(element);
				}
				else if(text && element.tagName().equals("html:text"))
				{
					System.out.println("Processing html:text");
					processHtmlTextTag(element);
				}
				else if(multibox && element.tagName().equals("html:multibox"))
				{
					System.out.println("Processing html:multibox");
					processHtmlMultibox(element);
				}
				else if(multibox && element.tagName().equals("logic:iterate"))
				{
					System.out.println("Processing html:iterate");
					processLogicIterateTag(element);
				}
			}
			writeToFile(doc);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}

	public static boolean isEmptyOrNull(String data)
	{	
		return (data == null || data.isEmpty() );
	}

	private static void writeToFile(Document doc)
	{
		try {
			path = path.replace(".", "_new.");
			final File f = new File(path);
			FileUtils.writeStringToFile(f, Parser.unescapeEntities(doc.html(), false), "UTF-8");
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	private static void processBeanWriteTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");

		if(isEmptyOrNull(name) && isEmptyOrNull(property))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		element.replaceWith(new TextNode("${"+name+property+"}"));
	}


	private static void processHtmlHiddenTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");

		if(!isEmptyOrNull(name) || isEmptyOrNull(property))
			return;

		element.tagName("form:hidden");
		element.attr("path",property);
		element.removeAttr("property");
	}

	private static void processHtmlMultibox(Element element)
	{
		String property = element.attr("property");

		if( isEmptyOrNull(property))
			return;

		element.tagName("form:select");
		element.attr("path",property);
		element.removeAttr("property");
	}

	private static void processHtmlTextTag(Element element)
	{
		String property = element.attr("property");

		if( isEmptyOrNull(property))
			return;

		element.tagName("form:input");
		element.attr("path",property);
		element.removeAttr("property");
	}

	private static void processLogicEqualTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");
		String value = element.attr("value");

		if(isEmptyOrNull(name) || isEmptyOrNull(property) || isEmptyOrNull(value))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		if(value.contains("<%"))
			value = value.replace("<","")
			.replace("%","")
			.replace("=","")
			.replace(">","");


		element.tagName("c:if");
		element.attr("test","${"+name+property+" eq "+value+"}");
		element.removeAttr("name");
		element.removeAttr("property");
		element.removeAttr("value");
	}

	private static void processHtmlSelectNoptionTag(Element element)
	{
		if(element.tagName().equals("html:option"))
		{
			element.tagName("form:option");
			return;
		}
		if(element.tagName().equals("html:select"))
		{
			element.tagName("form:select");
			String property = element.attr("property");
			String styleClass = element.attr("styleClass");
			if(!isEmptyOrNull(property))
			{
				element.attr("path",property);
				element.removeAttr("property");
				element.removeAttr("name");
			}
			if(!isEmptyOrNull(styleClass))
			{
				element.attr("cssClass",styleClass);
				element.removeAttr("styleClass");
			}
			return;
		}

		if(element.tagName().equals("html:options"))
		{
			String collection = element.attr("collection");
			String label = element.attr("labelProperty");
			String value = element.attr("property");

			if(isEmptyOrNull(collection) || isEmptyOrNull(label) || isEmptyOrNull(value))
				return;

			element.tagName("form:options");
			element.attr("items","${"+collection+"}");
			element.attr("itemLabel",label);
			element.attr("itemValue",value);

			element.removeAttr("collection");
			element.removeAttr("labelProperty");
			element.removeAttr("property");
		}

	}

	private static void processLogicNotEqualTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");
		String value = element.attr("value");

		if(isEmptyOrNull(name) || isEmptyOrNull(property) || isEmptyOrNull(value))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		if(value.contains("<%"))
			value = value.replace("<","")
			.replace("%","")
			.replace("=","")
			.replace(">","");


		element.tagName("c:if");
		element.attr("test","${"+name+property+" ne "+value+"}");
		element.removeAttr("name");
		element.removeAttr("property");
		element.removeAttr("value");

	}

	private static void processLogicPresentTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");

		if(isEmptyOrNull(name) || isEmptyOrNull(property))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		element.tagName("c:if");
		element.attr("test","${not empty "+name+property+"}");
		element.removeAttr("name");
		element.removeAttr("property");

	}

	private static void processLogicEmtpyTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");

		if(isEmptyOrNull(name) || isEmptyOrNull(property))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		element.tagName("c:if");
		element.attr("test","${empty "+name+property+"}");
		element.removeAttr("name");
		element.removeAttr("property");
	}

	private static void processLogicNotEmtpyTag(Element element)
	{
		String name = element.attr("name");
		String property = element.attr("property");

		if(isEmptyOrNull(name) || isEmptyOrNull(property))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		element.tagName("c:if");
		element.attr("test","${not empty "+name+property+"}");
		element.removeAttr("name");
		element.removeAttr("property");
	}

	private static void processLogicIterateTag(Element element)
	{
		String id = element.attr("id");
		String name = element.attr("name");
		String property = element.attr("property");

		if(isEmptyOrNull(id) || isEmptyOrNull(name) || isEmptyOrNull(property))
			return;

		if(!isEmptyOrNull(property))
			property = "."+property;

		element.tagName("c:forEach");
		element.attr("items","${"+name+property+"}");
		element.attr("var",id);

		element.removeAttr("id");
		element.removeAttr("name");
		element.removeAttr("property");

	}
}
