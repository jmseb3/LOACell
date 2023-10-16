import SwiftUI
import SwiftUI_Snackbar
import shared

struct ContentView: View {
    @StateObject var viewModel: LoaCellViewModel = LoaCellViewModel()

    let bottomHeight : CGFloat = 80
    var body: some View {
        VStack {
            if(viewModel.user == nil) {
                LoginView()
            } else {
                VStack {
                    DialogHost() {
                        SnackBarHost(bottomSpace: bottomHeight) {
                            TopAppBar()
                            Divider()
                            MainContent()
                            BottomAppBar(height: bottomHeight)
                        }
                    }
                }
            }
        }
        .environmentObject(viewModel)
        .environmentObject(viewModel.sc)
    }
}
