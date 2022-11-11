		$(document)
				.ready(
						function() {
							$("#cancel").on("click", function() {
								window.location = moduleURL;
							});
							$("#fileImage")
									.change(
											function() {
												fileSize = this.files[0].size;
												//alert("File size" + fileSize)
												if (fileSize > 502400) {
													this.setCustomValidity("You must choose an image less  than 500KB");
													this.reportValidity();
												} else {
													this.setCustomValidity("");
													showImageThumbnail(this);
												}

											});
						});
		function showImageThumbnail(fileInput) {
			var file = fileInput.files[0];
			var reader = new FileReader();
			reader.onload = function(e) {
				$("#thumbnail").attr("src", e.target.result);
			}
			reader.readAsDataURL(file);
		}
		function showModalDialog(title, message) {
			$("#modalTitle").text(title);
			$("#modalBody").text(message);
			$("#modalDialog").modal();
		}
		function showWarningModal(message) {
			showModalDialog("Warning", message);
		}
		function showErrorModal(message) {
			showModalDialog("Error", message);
		}