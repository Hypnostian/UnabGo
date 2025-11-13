import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import axios from "axios";
import * as cheerio from "cheerio";

admin.initializeApp();
const db = admin.firestore();

/**
 * Scraping del listado de noticias UNAB
 * (Luego ajustaremos estos selectores según el HTML real)
 */
async function scrapeUnabNews() {
  const url = "https://unab.edu.co/noticias/";
  const response = await axios.get(url, {
    headers: {
      "User-Agent": "Mozilla/5.0",
    },
  });

  const $ = cheerio.load(response.data);
  const articles: any[] = [];

  $("article").each((_, el) => {
    const title = $(el).find("h2, h3").first().text().trim();
    const link = $(el).find("a").attr("href") || "";
    const imageUrl = $(el).find("img").attr("src") || "";
    const summary = $(el).find("p").text().trim();
    const date = $(el).find("time").text().trim();

    if (title && link) {
      const id = Buffer.from(link).toString("base64");

      articles.push({
        id,
        title,
        imageUrl,
        url: link,
        date,
        summary,
        category: "Institucional",
        contentHtml: "",
        createdAt: new Date().toISOString(),
      });
    }
  });

  return articles;
}

/**
 * Scraping del contenido HTML de una noticia individual
 */
async function scrapeArticleContent(articleUrl: string) {
  try {
    const response = await axios.get(articleUrl, {
      headers: {
        "User-Agent": "Mozilla/5.0",
      },
    });

    const $ = cheerio.load(response.data);

    const content =
      $(".content").html() ||
      $(".entry-content").html() ||
      $("article").html() ||
      "";

    return content.trim();
  } catch (e) {
    console.log("Error al obtener contenido:", e);
    return "";
  }
}

/**
 * Función programada cada 15 minutos para buscar nuevas noticias
 * Si encuentra una noticia nueva, la guarda y envía notificación al topic
 */
export const updateUnabNews = functions.pubsub
  .schedule("every 15 minutes")
  .onRun(async () => {
    const scraped = await scrapeUnabNews();

    for (const a of scraped) {
      const ref = db.collection("news").doc(a.id);
      const exists = await ref.get();

      if (!exists.exists) {
        const htmlContent = await scrapeArticleContent(a.url);

        await ref.set({
          ...a,
          contentHtml: htmlContent,
        });

        await admin.messaging().sendToTopic("unab_news", {
          notification: {
            title: "Nueva noticia UNAB",
            body: a.title,
          },
          data: {
            newsId: a.id,
          },
        });
      }
    }

    return null;
  });

/**
 * API pública para consumir desde Retrofit (AvisosScreen)
 */
export const unabNewsApi = functions.https.onRequest(async (req, res) => {
  try {
    const snap = await db
      .collection("news")
      .orderBy("createdAt", "desc")
      .get();

    const docs = snap.docs.map((d) => d.data());

    res.set("Access-Control-Allow-Origin", "*");
    res.status(200).json(docs);
  } catch (e) {
    console.error("Error consultando noticias:", e);
    res.status(500).json({ error: "Error fetching news" });
  }
});
