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
      assigneeName: issue.assigneeName || "-",
      estStartDate: normalizeDate(issue.estStartDate),
      dueDate: normalizeDate(issue.dueDate),
      startDate: normalizeDate(issue.startDate),
      closedDate: normalizeDate(issue.closedDate),
      doneRatio:
        issue.doneRatio == null || issue.doneRatio === "" ? "-" : issue.doneRatio,
    };
  });

  var isPanel = document.body.classList.contains("role-panel-embed");

  var grid = new tui.Grid({
    el: gridEl,
    data: data,
    scrollX: isPanel,
    scrollY: false,
    bodyHeight: isPanel ? 280 : 320,
    rowHeaders: [],
    pageOptions: {
      useClient: true,
      perPage: 10,
    },
    columns: [
      { header: "우선순위", name: "priorityCd", align: "center", width: 72 },
      {
        header: "이슈명",
        name: "subject",
        align: "left",
        minWidth: isPanel ? 120 : 160,
        sortable: true,
        formatter: function (cell) {
          var issueId = cell.row.issueId;
          var text = cell.value == null || cell.value === "" ? "-" : String(cell.value);
          if (!issueId) return text;
          if (isPanel) {
            return (
              '<button type="button" class="role-name-link mem-issue-subject-link">' +
              text +
              "</button>"
            );
          }
          return (
            '<a href="/project/issue/detail?id=' +
            encodeURIComponent(String(issueId)) +
            '">' +
            text +
            "</a>"
          );
        },
      },
      { header: "유형", name: "categoryCd", align: "center", width: 84, sortable: true },
      { header: "상태", name: "statusCd", align: "center", width: 84, sortable: true },
      { header: "담당자", name: "assigneeName", align: "center", width: 84, sortable: true },
      { header: "시작예정일", name: "estStartDate", align: "center", width: 96 },
      { header: "완료예정일", name: "dueDate", align: "center", width: 96, sortable: true },
      { header: "시작일", name: "startDate", align: "center", width: 96, sortable: true },
      { header: "완료일", name: "closedDate", align: "center", width: 96, sortable: true },
      { header: "진척율(%)", name: "doneRatio", align: "center", width: 80, sortable: true },
    ],
  });

  if (isPanel) {
    grid.on("click", function (ev) {
      if (ev.columnName !== "subject" || ev.rowKey == null) return;
      var row = grid.getRow(ev.rowKey);
      if (!row || row.issueId == null || row.issueId === "") return;
      var topWin = window.top || window;
      topWin.location.href =
        "/project/issue/detail?id=" +
        encodeURIComponent(String(row.issueId));
    });
  }

  if (!data.length) {
    grid.resetData([]);
  }
})();
