document.addEventListener('DOMContentLoaded', () => {
    const queryForm = document.getElementById('query-form');
    const questionInput = document.getElementById('question-input');
    const askButton = document.getElementById('ask-button');
    const questionError = document.getElementById('question-error');
    const queryErrorAlert = document.getElementById('query-error-alert');

    // Display area containers and content elements
    const promptDisplay = document.getElementById('prompt-display');
    const promptContent = document.getElementById('prompt-content');
    const rawResponseDisplay = document.getElementById('raw-response-display');
    const rawResponseContent = document.getElementById('raw-response-content');
    const answerDisplay = document.getElementById('answer-display');
    const answerContent = document.getElementById('answer-content');
    const sourcesDisplay = document.getElementById('sources-display');
    const sourcesContent = document.getElementById('sources-content');

    const QUERY_API_URL = '/api/rag/query';

    // Hide all result areas initially (optional, could also be done via CSS)
    [promptDisplay, rawResponseDisplay, answerDisplay, sourcesDisplay].forEach(el => el?.classList.add('hidden'));

    if (queryForm) {
        queryForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Prevent default form submission

            const question = questionInput.value.trim();

            // Validation
            if (!question) {
                if (questionError) {
                    questionError.classList.remove('hidden');
                }
                questionInput.focus();
                return;
            } else {
                if (questionError) {
                    questionError.classList.add('hidden');
                }
            }

            // Hide previous errors and results
            if (queryErrorAlert) queryErrorAlert.classList.add('hidden');
            [promptDisplay, rawResponseDisplay, answerDisplay, sourcesDisplay].forEach(el => el?.classList.add('hidden'));

            // Loading state
            askButton.disabled = true;
            const originalButtonText = askButton.textContent;
            askButton.textContent = 'Asking...';
            // Optional: Add spinner icon here

            try {
                await submitQuery(question);
            } catch (error) {
                console.error('Error submitting query:', error);
                if (queryErrorAlert) {
                    queryErrorAlert.textContent = `An error occurred: ${error.message || 'Please try again.'}`;
                    queryErrorAlert.classList.remove('hidden');
                }
            } finally {
                // Restore button state
                askButton.disabled = false;
                askButton.textContent = originalButtonText;
                // Optional: Remove spinner icon here
            }
        });
    }

    async function submitQuery(question) {
        const response = await fetch(QUERY_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Add CSRF token header if needed (e.g., for Spring Security)
                // 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify({ question: question })
        });

        if (!response.ok) {
            let errorMessage = `HTTP error! Status: ${response.status}`;
            try {
                const errorData = await response.json();
                errorMessage += ` - ${errorData.message || errorData.error || 'Unknown server error'}`;
            } catch (e) {
                // Ignore if response body is not JSON or empty
            }
            throw new Error(errorMessage);
        }

        const data = await response.json(); // Expected: { "prompt": "...", "response": "..." }
        displayResults(data.prompt, data.response);
        if (queryErrorAlert) queryErrorAlert.classList.add('hidden'); // Hide error if successful
    }

    function displayResults(promptText, xmlResponseText) {
        // Display Prompt
        if (promptContent && promptDisplay) {
            promptContent.textContent = promptText;
            promptDisplay.classList.remove('hidden');
        }

        // Display Raw Response
        if (rawResponseContent && rawResponseDisplay) {
            rawResponseContent.textContent = xmlResponseText;
            rawResponseDisplay.classList.remove('hidden');
        }

        // Parse XML and display formatted results
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlResponseText, "text/xml");

            // Check for parser errors (often indicated by a specific root element)
            const parseError = xmlDoc.querySelector('parsererror');
            if (parseError) {
                console.error('XML Parsing Error:', parseError.textContent);
                throw new Error('Could not parse the XML response.');
            }

            // Extract Answer
            const answerElement = xmlDoc.querySelector('antwort');
            const answer = answerElement ? answerElement.textContent : 'Could not parse answer from XML.';
            if (answerContent && answerDisplay) {
                answerContent.textContent = answer; // Use textContent for safety unless HTML is expected and sanitized
                answerDisplay.classList.remove('hidden');
            }

            // Extract Sources
            const sourcesList = xmlDoc.querySelectorAll('quellen quelle');
            let sourcesHtml = '<p>No sources found in the response.</p>';
            if (sourcesList.length > 0) {
                sourcesHtml = '<ul class="list-disc list-inside space-y-1">';
                sourcesList.forEach(source => {
                    // Basic sanitization: display as text content
                    const sourceText = source.textContent || '';
                    sourcesHtml += `<li>${sourceText.replace(/</g, "<").replace(/>/g, ">")}</li>`;
                });
                sourcesHtml += '</ul>';
            }

            if (sourcesContent && sourcesDisplay) {
                sourcesContent.innerHTML = sourcesHtml; // Using innerHTML for the list structure
                sourcesDisplay.classList.remove('hidden');
            }

        } catch (error) {
            console.error('Error processing XML response:', error);
            if (queryErrorAlert) {
                queryErrorAlert.textContent = `Error processing response: ${error.message}`;
                queryErrorAlert.classList.remove('hidden');
            }
            // Optionally hide parts of the results if parsing failed
            if (answerDisplay) answerDisplay.classList.add('hidden');
            if (sourcesDisplay) sourcesDisplay.classList.add('hidden');
        }
    }
});