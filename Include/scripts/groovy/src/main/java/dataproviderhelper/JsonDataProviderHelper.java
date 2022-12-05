/*
 * package dataproviderhelper;
 * 
 * import java.io.FileNotFoundException;
 * 
 * import org.bson.json.JsonReader; import
 * org.delta.automation.automationcoreframework.logger.Log; import
 * org.json.simple.JSONObject; import org.json.simple.parser.JSONParser;
 * 
 * import java.io.FileReader; import java.io.IOException;
 * 
 * import java.io.Reader; import java.util.ArrayList; import java.util.Arrays;
 * import java.util.List; import org.json.simple.parser.ParseException;
 * 
 * public class JsonDataProviderHelper {
 * 
 * public static FileReader getReader(String file) { FileReader reader = null;
 * try { reader = new FileReader(file); } catch (FileNotFoundException e) {
 * throw new RuntimeException(e); }
 * 
 * return reader; }
 * 
 * public static void closeReader(FileReader reader) { try { if(reader != null)
 * { reader.close(); } } catch (IOException e) { e.printStackTrace(); } }
 *//**
	 * extractData_JSON method to get JSON data from file
	 *
	 * @param file
	 * @return JSONObject
	 *//*
		 * public static JSONObject extractData_JSON(String file) { JSONObject
		 * theJSONObject = null; FileReader reader = null; try { reader =
		 * getReader(file); JSONParser jsonParser = new JSONParser(); theJSONObject =
		 * (JSONObject) jsonParser.parse(reader); } catch (FileNotFoundException e) {
		 * throw new RuntimeException(e); } catch (IOException | ParseException e) {
		 * throw new RuntimeException(e); } finally { try { if(reader != null) {
		 * reader.close(); } } catch (IOException e) { e.printStackTrace(); } } return
		 * theJSONObject; }
		 * 
		 * public static List<JSONObject> filterTestData(List<JSONObject> testDataList)
		 * { // include Filter if (System.getProperty("includePattern") != null) {
		 * String include = System.getProperty("includePattern"); List<JSONObject>
		 * newList = new ArrayList<>(); List<String> tests =
		 * Arrays.asList(include.split(",", -1)); if (!(tests.isEmpty() ||
		 * tests.contains("NONE"))) {
		 * Log.LOGGER.info("Filtering the test data based on the following patterns: " +
		 * tests); } else { return testDataList; } for (String getTest : tests) { for
		 * (int i = 0; i < testDataList.size(); i++) { if
		 * (testDataList.get(i).toString().contains(getTest)) {
		 * newList.add(testDataList.get(i)); } } }
		 * 
		 * // reassign testRows after filtering tests testDataList = newList;
		 * Log.LOGGER.info("Filtered Test Data List based on parameters: " +
		 * testDataList); }
		 * 
		 * // exclude Filter if (System.getProperty("excludePattern") != null) { String
		 * exclude = System.getProperty("excludePattern"); List<String> tests =
		 * Arrays.asList(exclude.split(",", -1));
		 * 
		 * for (String getTest : tests) { // start at end of list and work backwards so
		 * index stays in sync for (int i = testDataList.size() - 1; i >= 0; i--) { if
		 * (testDataList.get(i).toString().contains(getTest)) {
		 * testDataList.remove(testDataList.get(i)); } } } } return testDataList; } }
		 */