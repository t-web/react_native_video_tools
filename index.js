
import { NativeModules, DeviceEventEmitter } from 'react-native';

const { VideoTools } = NativeModules;
const onCompressProgress = 'onCompressProgress';

export default class VideoToolsComponent {

  static getVideoInfo(uri) {
    return VideoTools.getVideoInfo(uri);
  }

  static compress(uri) {
    return VideoTools.compress(uri);
  }

  static addEventListener(handler) {
    const listener = DeviceEventEmitter.addListener(
        onCompressProgress,
        (loc) => {
          handler(loc);
        }
    );
    return listener;
  }
}
