document.querySelectorAll('.juror-checkbox').forEach(jurorCheckbox => {
    jurorCheckbox.addEventListener('change', function() {
        const participantId = this.getAttribute('data-participant-id');
        const participantCheckbox = document.getElementById(participantId);

        console.log(`Juror checkbox changed: ${this.id}, Participant ID: ${participantId}`);
        console.log('Participant checkbox:', participantCheckbox);

        if (participantCheckbox) {
            if (this.checked) {
                participantCheckbox.disabled = true;
                participantCheckbox.parentElement.querySelector('label').style.color = 'gray';
            } else {
                participantCheckbox.disabled = false;
                participantCheckbox.parentElement.querySelector('label').style.color = '';
            }
        } else {
            console.error('Participant checkbox not found for ID:', participantId);
        }
    });
});

document.querySelectorAll('.participant-checkbox').forEach(participantCheckbox => {
    participantCheckbox.addEventListener('change', function() {
        const jurorId = this.getAttribute('data-juror-id');
        const jurorCheckbox = document.getElementById(jurorId);

        console.log(`Participant checkbox changed: ${this.id}, Juror ID: ${jurorId}`);
        console.log('Juror checkbox:', jurorCheckbox);

        if (jurorCheckbox) {
            if (this.checked) {
                // Disable and gray out the corresponding juror checkbox
                jurorCheckbox.disabled = true;
                jurorCheckbox.parentElement.querySelector('label').style.color = 'gray';
            } else {
                // Re-enable the corresponding juror checkbox
                jurorCheckbox.disabled = false;
                jurorCheckbox.parentElement.querySelector('label').style.color = '';
            }
        } else {
            console.error('Juror checkbox not found for ID:', jurorId);
        }
    });
});


