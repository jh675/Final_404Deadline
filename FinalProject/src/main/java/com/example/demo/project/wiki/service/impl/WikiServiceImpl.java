package com.example.demo.project.wiki.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.project.wiki.mapper.WikiMapper;
import com.example.demo.project.wiki.service.WikiContentVO;
import com.example.demo.project.wiki.service.WikiDateGroupVO;
import com.example.demo.project.wiki.service.WikiLinkSuggestVO;
import com.example.demo.project.wiki.service.WikiPageVO;
import com.example.demo.project.wiki.service.WikiService;

@Service
public class WikiServiceImpl implements WikiService {

	@Autowired
	WikiMapper mapper;
	
	@Override
	public WikiContentVO selectWikiContentLastVerByTitle(Long id,String title) {
		return mapper.selectWikiContentLastVerByTitle(id,title);
	}

	@Override
	public WikiContentVO selectWikiContentByPageIdAndVersion(Long pageId, Long version) {
		return mapper.selectWikiContentByPageIdAndVersion(pageId, version);
	}

	@Override
	public List<WikiContentVO> selectWikiHistoryByPageId(Long pageId) {
		List<WikiContentVO> history = mapper.selectWikiHistoryByPageId(pageId);
		return history != null ? history : Collections.emptyList();
	}

	private static final String DEFAULT_DESCRIBE = "-";

	private void normalizeWikiContent(WikiContentVO wikiContentVO) {
		if (wikiContentVO == null) {
			return;
		}
		if (wikiContentVO.getDescribe() == null || wikiContentVO.getDescribe().isBlank()) {
			wikiContentVO.setDescribe(DEFAULT_DESCRIBE);
		} else {
			wikiContentVO.setDescribe(wikiContentVO.getDescribe().trim());
		}
		if (wikiContentVO.getContent() == null) {
			wikiContentVO.setContent("");
		}
	}

	@Override
	public Long insertWikiContent(WikiContentVO wikiContentVO) {
		normalizeWikiContent(wikiContentVO);
		mapper.insertWikiContent(wikiContentVO);
		return wikiContentVO.getId();
	}

	@Override
	public Long updateWikiContent(WikiContentVO wikiContentVO) {
		normalizeWikiContent(wikiContentVO);
		return mapper.updateWikiContent(wikiContentVO);
	}

	@Override
	@Transactional
	public Long saveNewWiki(String title, long prjId, Long parentId, WikiContentVO content, Long memId) {
		Long pageId = insertWikiPage(title, prjId, parentId);
		content.setPageId(pageId);
		content.setTitle(title);
		content.setMemId(memId);
		insertWikiContent(content);
		return pageId;
	}

	@Override
	@Transactional
	public void reviseWiki(WikiContentVO content, Long memId) {
		content.setMemId(memId);
		updateWikiContent(content);
	}

	@Override
	public Long deleteWikiContent(Long id) {
		return mapper.deleteWikiContent(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageForDate(Long id) {
		return mapper.selectWikiPageForDate(id);
	}

	@Override
	public List<WikiPageVO> selectWikiPageListGroupParent(Long id) {
		return mapper.selectWikiPageListGroupParent(id);
	}

	@Override
	public Long nameCheck(Long id,String name) {
		return mapper.nameCheck(id,name);
	}

	@Override
	public List<WikiPageVO> selectWikiPageForTree(Long prjId, Long id) {
		return mapper.selectWikiPageForTree(prjId, id);
	}

	@Override
	public List<WikiPageVO> selectWikiIndexPages(Long projectId) {
		return mapper.selectWikiIndexPages(projectId);
	}

	@Override
	public List<WikiPageVO> getTitleTree(Long projectId) {
		List<WikiPageVO> pages = mapper.selectWikiIndexPages(projectId);
		if (pages == null || pages.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Long, WikiPageVO> pageMap = new LinkedHashMap<>();
		for (WikiPageVO page : pages) {
			page.setChildren(new ArrayList<>());
			pageMap.put(page.getId(), page);
		}

		List<WikiPageVO> roots = new ArrayList<>();
		for (WikiPageVO page : pageMap.values()) {
			Long parentId = page.getParentId();
			if (parentId == null || parentId.equals(page.getId()) || !pageMap.containsKey(parentId)) {
				roots.add(page);
				continue;
			}
			pageMap.get(parentId).getChildren().add(page);
		}

		sortTreeByTitle(roots);
		return roots;
	}

	@Override
	public List<WikiDateGroupVO> getDateGroups(Long projectId) {
		List<WikiPageVO> pages = mapper.selectWikiIndexPages(projectId);
		if (pages == null || pages.isEmpty()) {
			return Collections.emptyList();
		}

		List<WikiPageVO> sortedPages = new ArrayList<>(pages);
		sortedPages.sort(Comparator
				.comparing(WikiPageVO::getUpdatedOn, Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(this::compareTitle));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Map<String, List<WikiPageVO>> groupedPages = new LinkedHashMap<>();
		for (WikiPageVO page : sortedPages) {
			String groupKey = page.getUpdatedOn() != null
					? formatter.format(page.getUpdatedOn())
					: "날짜 미상";
			groupedPages.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(page);
		}

		List<WikiDateGroupVO> groups = new ArrayList<>();
		for (Map.Entry<String, List<WikiPageVO>> entry : groupedPages.entrySet()) {
			WikiDateGroupVO group = new WikiDateGroupVO();
			group.setGroupDate(entry.getKey());
			group.setPages(entry.getValue());
			groups.add(group);
		}
		return groups;
	}

	@Override
	public Long insertWikiPage(String title, long prjId, Long parentId) {
		WikiPageVO page = new WikiPageVO();
		page.setTitle(title);
		page.setPrjId(prjId);
		page.setParentId(parentId);
		mapper.insertWikiPage(page);
		return page.getId();
	}

	@Override
	public Long updateWikiPageParent(Long pageId, Long parentId) {
		WikiPageVO page = new WikiPageVO();
		page.setId(pageId);
		page.setParentId(parentId);
		return mapper.updateWikiPageParent(page);
	}

	@Override
	public WikiContentVO selectWikiContentLastVerById(Long id) {
		return mapper.selectWikiContentLastVerById(id);
	}

	@Override
	public List<WikiPageVO> getBreadcrumb(Long pageId) {
		if (pageId == null) {
			return Collections.emptyList();
		}
		List<WikiPageVO> breadcrumb = mapper.selectWikiBreadcrumb(pageId);
		return breadcrumb != null ? breadcrumb : Collections.emptyList();
	}

	@Override
	public boolean hasWikiChildren(Long pageId) {
		if (pageId == null) {
			return false;
		}
		Long count = mapper.countWikiChildren(pageId);
		return count != null && count > 0;
	}

	@Override
	public List<WikiLinkSuggestVO> suggestWikiLinks(Long projectId, String q) {
		if (projectId == null) {
			return Collections.emptyList();
		}
		String term = q != null ? q.trim() : "";
		List<String> titles = mapper.searchWikiTitles(projectId, term.isEmpty() ? null : term);
		if (titles == null || titles.isEmpty()) {
			return Collections.emptyList();
		}
		List<WikiLinkSuggestVO> result = new ArrayList<>();
		for (String title : titles) {
			if (title == null || title.isBlank()) {
				continue;
			}
			result.add(new WikiLinkSuggestVO("wiki", title, "[[" + title + "]]"));
		}
		return result;
	}

	private void sortTreeByTitle(List<WikiPageVO> nodes) {
		nodes.sort(this::compareTitle);
		for (WikiPageVO node : nodes) {
			if (node.getChildren() != null && !node.getChildren().isEmpty()) {
				sortTreeByTitle(node.getChildren());
			}
		}
	}

	private int compareTitle(WikiPageVO left, WikiPageVO right) {
		String leftTitle = left != null && left.getTitle() != null ? left.getTitle() : "";
		String rightTitle = right != null && right.getTitle() != null ? right.getTitle() : "";
		return leftTitle.compareToIgnoreCase(rightTitle);
	}
}
