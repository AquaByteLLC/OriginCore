package commons.util;

import commons.interpolation.impl.InterpolationType;

import java.awt.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vadim
 */
public class test {

	private static final Pattern GRADIENT = Pattern.compile("&#<(?<hex>(?:[0-9A-Fa-f]{6}:)*[0-9A-Fa-f]{6});(?<mode>[a-zA-Z]+)(?:\\((?<args>(?:[.\\d]+,)*[.\\d]+)\\))?>(?<str>.+?)(&r|$)");

	public static void main(String[] args) {
		System.out.println(parseGradients("&#<000000:111111:222222:efefef;BounceOut(1,2,3,4)>a multi message&&ae&e&r&#<ffffff;BounceIn(1)>More colors!&rOut"));
		System.out.println(parseGradients("&#<000000:111111:222222:efefef;BounceOut(1,2,3,4)>More of my&&ae &e &t don't do this btw message&rOutside of message&r&r"));
		System.out.println(parseGradients("&#<000000:111111:222222:efefef;BounceOut(1.1,2.3,.34,45)>a ith"));
		System.out.println(parseGradients("&#<000000:111111:222222:efefef;BounceOut>noargs"));
	}

	public static String parseGradients(String message) {
		System.out.println();
		if (message == null) return null;
		int end = -1;

		StringBuilder builder = new StringBuilder();

		Matcher matcher = GRADIENT.matcher(message);
		while (matcher.find()) {
			String[]          hex  = matcher.group("hex").split(":");
			InterpolationType mode = InterpolationType.fromString(matcher.group("mode"));
			String            args = matcher.group("args");
			String            str  = matcher.group("str");

			Color[] colors = new Color[hex.length];
			for (int i = 0; i < hex.length; i++)
				 colors[i] = new Color(Integer.valueOf(hex[i], 16));

			double[] portions;
			if (args != null) {
				String[] split = args.split(",");
				portions = new double[split.length];

				for (int i = 0; i < split.length; i++)
					 portions[i] = Double.parseDouble(split[i]);
			} else
				portions = null;

			System.out.printf(">> %s %s(%s): %s%n", Arrays.toString(hex), mode, args, str);
			if (end > -1)
				builder.append(message, end, matcher.start());
			builder.append("[").append(str).append("]");
//				   .append(StringUtil.interpolateColor(str, colors, portions, mode))
			end = matcher.end();
		}

		if (end > -1)
			builder.append(message, end, message.length());

		return builder.toString();
	}

}
