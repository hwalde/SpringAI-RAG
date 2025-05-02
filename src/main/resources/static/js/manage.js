document.addEventListener('DOMContentLoaded', () => {
    const infoForm = document.getElementById('info-form');
    const infoIdInput = document.getElementById('info-id');
    const infoTitleInput = document.getElementById('info-title');
    const infoContentInput = document.getElementById('info-content');
    const titleError = document.getElementById('title-error');
    const contentError = document.getElementById('content-error');
    const contentCharCount = document.getElementById('content-char-count');
    const saveButton = document.getElementById('save-button');
    const infoTableBody = document.getElementById('info-table-body');
    const formErrorAlert = document.getElementById('form-error-alert');

    const API_URL = '/api/information';

    // --- Helper Functions ---

    const displayFormError = (message) => {
        formErrorAlert.textContent = message;
        formErrorAlert.classList.remove('hidden');
        formErrorAlert.classList.remove('bg-green-100', 'border-green-400', 'text-green-700');
        formErrorAlert.classList.add('bg-red-100', 'border-red-400', 'text-red-700');
    };

    const displayFormSuccess = (message) => {
        formErrorAlert.textContent = message;
        formErrorAlert.classList.remove('hidden');
        formErrorAlert.classList.remove('bg-red-100', 'border-red-400', 'text-red-700');
        formErrorAlert.classList.add('bg-green-100', 'border-green-400', 'text-green-700');
         // Optionally hide after a delay
        setTimeout(hideFormError, 3000);
    };

    const hideFormError = () => {
        formErrorAlert.classList.add('hidden');
        formErrorAlert.textContent = '';
    };

    const resetForm = () => {
        infoForm.reset();
        infoIdInput.value = '';
        titleError.classList.add('hidden');
        contentError.classList.add('hidden');
        infoTitleInput.classList.remove('border-red-500');
        infoContentInput.classList.remove('border-red-500');
        saveButton.textContent = 'Save Information';
        contentCharCount.textContent = '0 / 1000';
        hideFormError();
    };

    const validateForm = () => {
        let isValid = true;
        hideFormError(); // Clear previous general errors
        titleError.classList.add('hidden');
        infoTitleInput.classList.remove('border-red-500');
        contentError.classList.add('hidden');
        infoContentInput.classList.remove('border-red-500');


        if (!infoTitleInput.value.trim()) {
            titleError.textContent = 'Title cannot be empty.';
            titleError.classList.remove('hidden');
            infoTitleInput.classList.add('border-red-500');
            isValid = false;
        }

        if (!infoContentInput.value.trim()) {
            contentError.textContent = 'Content cannot be empty.';
            contentError.classList.remove('hidden');
            infoContentInput.classList.add('border-red-500');
            isValid = false;
        } else if (infoContentInput.value.length > 1000) { // Example max length
             contentError.textContent = 'Content cannot exceed 1000 characters.';
             contentError.classList.remove('hidden');
             infoContentInput.classList.add('border-red-500');
             isValid = false;
        }


        return isValid;
    };

    // --- API Call Functions ---

    const fetchInformation = async () => {
        try {
            const response = await fetch(API_URL);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const informationList = await response.json();
            renderTable(informationList);
            hideFormError(); // Hide error if fetch succeeds
        } catch (error) {
            console.error('Error fetching information:', error);
            displayFormError('Failed to load information. Please try again later.');
        }
    };

    const fetchSingleInformation = async (id) => {
         try {
            const response = await fetch(`${API_URL}/${id}`);
            if (!response.ok) {
                 throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
         } catch (error) {
            console.error(`Error fetching information item ${id}:`, error);
            displayFormError(`Failed to load item ${id}. Please try again.`);
            return null; // Indicate failure
         }
    };

    const createInformation = async (data) => {
        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Unknown error occurred.' }));
                throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || 'Failed to create.'}`);
            }
            resetForm();
            fetchInformation(); // Refresh table
            displayFormSuccess('Information created successfully!');
        } catch (error) {
            console.error('Error creating information:', error);
            displayFormError(`Failed to create information: ${error.message}`);
        }
    };

    const updateInformation = async (id, data) => {
        try {
            const response = await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                 const errorData = await response.json().catch(() => ({ message: 'Unknown error occurred.' }));
                 throw new Error(`HTTP error! status: ${response.status} - ${errorData.message || 'Failed to update.'}`);
            }
            resetForm();
            fetchInformation(); // Refresh table
            displayFormSuccess('Information updated successfully!');
        } catch (error) {
            console.error('Error updating information:', error);
            displayFormError(`Failed to update information: ${error.message}`);
        }
    };

    const deleteInformation = async (id) => {
        try {
            const response = await fetch(`${API_URL}/${id}`, {
                method: 'DELETE',
            });
            if (!response.ok) {
                 throw new Error(`HTTP error! status: ${response.status}`);
            }
            fetchInformation(); // Refresh table
            displayFormSuccess('Information deleted successfully!'); // Use success for delete confirmation
        } catch (error) {
            console.error('Error deleting information:', error);
            displayFormError('Failed to delete information. Please try again.');
        }
    };


    // --- UI Rendering ---

    const renderTableRow = (info) => {
        const tr = document.createElement('tr');
        tr.classList.add('border-b', 'border-gray-200'); // Add some basic styling

        const tdTitle = document.createElement('td');
        tdTitle.textContent = info.title;
        tdTitle.classList.add('py-2', 'px-4'); // Add padding

        const tdContent = document.createElement('td');
        // Truncate content for display
        const truncatedContent = info.content.length > 50 ? info.content.substring(0, 50) + '...' : info.content;
        tdContent.textContent = truncatedContent;
        tdContent.classList.add('py-2', 'px-4'); // Add padding

        const tdActions = document.createElement('td');
        tdActions.classList.add('py-2', 'px-4'); // Add padding

        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.dataset.id = info.id;
        editButton.classList.add('text-indigo-600', 'hover:text-indigo-900', 'mr-3', 'font-medium');
        editButton.addEventListener('click', handleEditClick);

        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Delete';
        deleteButton.dataset.id = info.id;
        deleteButton.classList.add('text-red-600', 'hover:text-red-900', 'font-medium');
        deleteButton.addEventListener('click', handleDeleteClick);

        tdActions.appendChild(editButton);
        tdActions.appendChild(deleteButton);

        tr.appendChild(tdTitle);
        tr.appendChild(tdContent);
        tr.appendChild(tdActions);

        infoTableBody.appendChild(tr);
    };

    const renderTable = (informationList) => {
        // Clear existing table content
        infoTableBody.innerHTML = '';
        if (informationList && informationList.length > 0) {
            informationList.forEach(renderTableRow);
        } else {
            // Optional: Display a message if the table is empty
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 3; // Span across all columns
            td.textContent = 'No information available.';
            td.classList.add('text-center', 'py-4', 'text-gray-500');
            tr.appendChild(td);
            infoTableBody.appendChild(tr);
        }
    };

    // --- Event Handlers ---

    const handleFormSubmit = (event) => {
        event.preventDefault(); // Prevent default browser submission

        if (!validateForm()) {
            return; // Stop if validation fails
        }

        const id = infoIdInput.value;
        const title = infoTitleInput.value.trim();
        const content = infoContentInput.value.trim();

        const data = { title, content };

        if (id) {
            // Update
            data.id = id; // Include ID for update consistency if needed by backend
            updateInformation(id, data);
        } else {
            // Create
            createInformation(data);
        }
    };

    const handleEditClick = async (event) => {
        const id = event.target.dataset.id;
        if (!id) return;

        resetForm(); // Reset form before populating
        const info = await fetchSingleInformation(id);

        if (info) {
            infoIdInput.value = info.id;
            infoTitleInput.value = info.title;
            infoContentInput.value = info.content;
            saveButton.textContent = 'Update Information';
            updateCharCount(); // Update char count for loaded content
            // Scroll form into view
            infoForm.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    };

    const handleDeleteClick = (event) => {
        const id = event.target.dataset.id;
        if (!id) return;

        if (confirm('Are you sure you want to delete this item?')) {
            deleteInformation(id);
        }
    };

    const updateCharCount = () => {
        const currentLength = infoContentInput.value.length;
        const maxLength = 1000; // Match validation if needed
        contentCharCount.textContent = `${currentLength} / ${maxLength}`;
         // Optional: Add styling if exceeding limit (though validation handles submit)
        if (currentLength > maxLength) {
            contentCharCount.classList.add('text-red-600');
        } else {
            contentCharCount.classList.remove('text-red-600');
        }
    };


    // --- Initial Setup ---
    infoForm.addEventListener('submit', handleFormSubmit);
    infoContentInput.addEventListener('input', updateCharCount);
    // Add listener to reset button if it exists and needs specific JS logic beyond type="reset"
    const resetButton = infoForm.querySelector('button[type="reset"]');
    if (resetButton) {
        resetButton.addEventListener('click', resetForm); // Ensure our reset logic runs
    }


    fetchInformation(); // Initial data load
    updateCharCount(); // Initial char count update

});