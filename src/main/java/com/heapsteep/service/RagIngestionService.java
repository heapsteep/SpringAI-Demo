package com.heapsteep.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reads a PDF, splits it into chunks, embeds them, and stores them in pgvector.
 * Triggered on demand from {@code POST /ai/rag/upload}. Idempotent: if a PDF
 * with the same filename has already been ingested, ingestion is skipped.
 */
@Service
public class RagIngestionService {

    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);

    private final VectorStore vectorStore;

    public RagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestPdf(Resource pdf) {
        String source = pdf.getFilename() == null ? "unknown.pdf" : pdf.getFilename();

        if (alreadyIngested(source)) {
            log.info("[RAG] '{}' already ingested - skipping.", source);
            return;
        }

        log.info("[RAG] Ingesting '{}'...", source);

        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                pdf,
                PdfDocumentReaderConfig.builder()
                        .withPagesPerDocument(1)
                        .build()
        );

        List<Document> chunks = TokenTextSplitter.builder().build().apply(reader.read());
        chunks.forEach(d -> d.getMetadata().put("source", source));

        vectorStore.add(chunks);

        log.info("[RAG] Ingested '{}' as {} chunks.", source, chunks.size());
    }

    private boolean alreadyIngested(String source) {
        try {
            List<Document> hits = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(source)
                            .topK(1)
                            .filterExpression("source == '" + source + "'")
                            .build()
            );
            return hits != null && !hits.isEmpty();
        } catch (Exception e) {
            log.warn("[RAG] Could not check existing ingestion for '{}': {}", source, e.getMessage());
            return false;
        }
    }
}
