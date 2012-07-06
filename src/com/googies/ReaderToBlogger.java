package com.googies;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gdata.client.blogger.BloggerService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.util.ServiceException;

public class ReaderToBlogger {

	private static final String FOLDER = System.getProperty("user.dir") + "\\resources\\";
	private static final String SUCCESSFULLY_ADD = FOLDER + "successfullyAdd.txt";
	private static final String SHARED_ITEMS_JSON = FOLDER + "shared-items.json";
	private static final String FEED_URI_BASE = "http://www.blogger.com/feeds";
	

	public static void main(String[] args) {
		
		// Recuperation des donnees de l'utilisateur et du blog
		final Scanner scan = new Scanner(System.in);
		System.out.println("login ? ");
		final String login = scan.next();
		System.out.println("password ? ");
		final String password = scan.next();
		System.out.println("blog id ? ");
		final String blogId = scan.next();

		try {
			inject(blogId, login, password);
		} catch (ServiceException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void inject(final String blogId, final String userName, final String userPassword) throws ServiceException, IOException, InterruptedException {
		final BloggerService myService = new BloggerService(blogId);
		myService.setUserCredentials(userName, userPassword);

		// Donnees en entree
		final List liste = loadDataIn();
		
		// Post deja traite
		final File file = new File(SUCCESSFULLY_ADD);
		final List<String> successfullyAdd = FileUtils.readLines(file);
		
		// Boucle d'injection
		int cpt = 0;
		for (Object object : liste) {
		
			final Map map = (Map) object;
			final String crawlTimeMsec = (String) map.get("crawlTimeMsec");
			
			// On verifie si le post a deja ete traite ou non 
			if (successfullyAdd.contains(crawlTimeMsec)) {
				System.err.println("deja traite : " + crawlTimeMsec);
			} else {
				// On recupere les donnees
				final String title = StringUtils.remove((String) map.get("title"), "\n");
				final Long lg = new Long(crawlTimeMsec);
				final DateTime dt = new DateTime(new Date(lg), TimeZone.getTimeZone("Europe/Paris"));
				final List listeA = (List) map.get("alternate");
				String url = "";
				for (Object object2 : listeA) {
					Map alterMap = (Map) object2;
					if (alterMap.containsKey("href")) {
						url = (String) alterMap.get("href");
						break;
					}
				}
				
				// On injecte dans Blogger
				System.out.println("Element n° " + cpt);
				System.out.println("	crawlTimeMsec =" + crawlTimeMsec);
				System.out.println("	title =" + title);
				System.out.println("	url =" + url);
				final Entry newPost = createPost(myService, blogId, title, url, "moi", userName, dt);
				System.out.println("	Ajouté !");
				
				// On ajoute le post dans la liste des post deja traites
				successfullyAdd.add(crawlTimeMsec);
				FileUtils.writeLines(file, successfullyAdd);
				
				cpt++;
			}
		}
	}

	private static List loadDataIn() throws IOException, JsonParseException, JsonMappingException {
		final ObjectMapper mapper = new ObjectMapper();
		final Book book = mapper.readValue(new File(SHARED_ITEMS_JSON), Book.class);
		final List liste = book.getItems();
		return liste;
	}

	private static Entry createPost(final BloggerService myService, final String blogID, final String title, 
			final String content, final String authorName, final String userName, final DateTime dt)
			throws ServiceException, IOException {
		final Entry myEntry = new Entry();
		myEntry.setTitle(new PlainTextConstruct(title));
		myEntry.setContent(new PlainTextConstruct(content));
		final Person author = new Person(authorName, null, userName);
		myEntry.getAuthors().add(author);
		myEntry.setDraft(false);
		myEntry.setPublished(dt);
		final URL postUrl = new URL(FEED_URI_BASE + "/" + blogID + "/posts/default");
		return myService.insert(postUrl, myEntry);
	}

}
