import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()

    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView()
            } else {
                SnackBarHost() {
                    TopAppBar()
                    Divider()
                    MainContent()
                }
                BottomAppBar()
            }
        }
        .environmentObject(viewModel)
        .environmentObject(viewModel.sc)
    }
}
