// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable(
    {
      "lengthChange": false,
      "pageLength": 5
    }
  );
});
