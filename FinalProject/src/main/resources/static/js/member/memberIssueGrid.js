(function () {
  var gridEl = document.getElementById("memberIssueGrid");
  if (!gridEl || typeof tui === "undefined" || !tui.Grid) return;

  var rows = Array.isArray(window.memberIssueGridRows)
    ? window.memberIssueGridRows
    : [];

  function normalizeDate(value) {
    if (value == null || value === "") return "-";
    if (typeof value === "string") {
      if (/^\d{4}-\d{2}-\d{2}$/.test(value)) return value;
      if (value.length >= 10) return value.slice(0, 10);
      return value;
    }
    var d = new Date(value);
    if (isNaN(d.getTime())) return "-";
    var y = d.getFullYear();
    var m = String(d.getMonth() + 1).padStart(2, "0");
    var day = String(d.getDate()).padStart(2, "0");
    return y + "-" + m + "-" + day;
  }

  var data = rows.map(function (issue) {
    return {
      issueId: issue.issueId,
      priorityCd: issue.priorityCd || "-",
      subject: issue.subject || "-",
      categoryCd: issue.categoryCd || "-",
      statusCd: issue.statusCd || "-",
      requesterName: issue.requesterName || "-",
      assigneeName: issue.assigneeName || "-",
      estStartDate: normalizeDate(issue.estStartDate),
      dueDate: normalizeDate(issue.dueDate),
      startDate: normalizeDate(issue.startDate),
      closedDate: normalizeDate(issue.closedDate),
      doneRatio:
        issue.doneRatio == null || issue.doneRatio === "" ? "-" : issue.doneRatio,
    };
  });

  var grid = new tui.Grid({
    el: gridEl,
    data: data,
    scrollX: false,
    scrollY: false,
    bodyHeight: 360,
    rowHeaders: [],
    pageOptions: {
      useClient: true,
      perPage: 10,
    },
    columns: [
      { header: "우선순위", name: "priorityCd", align: "center", width: 100 },
      {
        header: "이슈명",
        name: "subject",
        align: "left",
        minWidth: 220,
        sortable: true,
        formatter: function (cell) {
          var issueId = cell.row.issueId;
          var text = cell.value == null || cell.value === "" ? "-" : String(cell.value);
          if (!issueId) return text;
          return (
            '<a href="/issue/detail?id=' +
            encodeURIComponent(String(issueId)) +
            '">' +
            text +
            "</a>"
          );
        },
      },
      { header: "이슈 타입", name: "categoryCd", align: "center", width: 120, sortable: true },
      { header: "이슈 상태", name: "statusCd", align: "center", width: 120, sortable: true },
      { header: "이슈 신청자", name: "requesterName", align: "center", width: 120, sortable: true },
      { header: "이슈 담당자", name: "assigneeName", align: "center", width: 120, sortable: true },
      { header: "시작예정일", name: "estStartDate", align: "center", width: 120 },
      { header: "마감희망일", name: "dueDate", align: "center", width: 120, sortable: true },
      { header: "시작일", name: "startDate", align: "center", width: 120, sortable: true },
      { header: "마감일", name: "closedDate", align: "center", width: 120, sortable: true },
      { header: "진척율(%)", name: "doneRatio", align: "center", width: 100, sortable: true },
    ],
  });

  if (!data.length) {
    grid.resetData([]);
  }
})();
