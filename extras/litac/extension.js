const vscode = require("vscode");
const vscode_languageclient = require("vscode-languageclient");

class FailFastErrorHandler {
    error(_error, _message, count) {
		console.log("Error: " + _message);
		console.log(_error);
		vscode.window.showErrorMessage("An error occurred: " + _message + " from " + _error );
        return ErrorAction.Shutdown;
    }
    closed() {
		vscode.window.showErrorMessage("The LitaC language server has crashed");
		return CloseAction.DoNotRestart;
    }
}

/**
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
	var config = vscode.workspace.getConfiguration("litac");
		
	var binaryPath = config.get("languageServerPath");
	if (!binaryPath) {
		vscode.window.showErrorMessage("Could not start LitaC language server due to missing setting: litac.languageServerPath");
		return;
	}

	var args = config.get("languageServerArguments");
	if (!args) {
		vscode.window.showErrorMessage("Could not start LitaC language server due to missing setting: litac.languageServerArguments");
		return;
	}
	
	var debugLog = config.get("languageServerLog");
	if (debugLog) {
		args += " -verbose";
	}
	
	var libraryPath = config.get("libraryPath");
	if (!libraryPath) {
		vscode.window.showErrorMessage("Could not start LitaC language server due to missing setting: litac.libraryPath");
		return;
	}
	else {
		args += " -lib " + libraryPath;
	}

	var failFast = (!!config.get("failFast")) || false;

	var serverOptions = {
		command: binaryPath,
		args: [ args ],
		options: { shell: true },
	};

	var clientOptions = {
		documentSelector: [{ scheme: 'file', language: 'litac' }],
		errorHandler: failFast ? new FailFastErrorHandler() : null,
	};

	var client = new vscode_languageclient.LanguageClient("litacLanguageServer", "LitaC language server", serverOptions, clientOptions);
	client.start();
}

function deactivate() {
}

module.exports = {
	activate,
	deactivate
}